package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.helpers.ProgressBar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.*;

/**
 * Class finds and holds variants.
 */
public class Caller {
  private final IndexedFastaSequenceFile fastaSequenceFile;
  private final List<SAMRecord> samRecords;
  private final Map<String, Map<Integer, Map<Allele, VariantInfo>>> variantInfoMap;

  /**
   * Constructor gets an indexed fasta sequence file
   * and a list of sam records matching the given intervals.
   */
  public Caller(IndexedFastaSequenceFile fastaSequenceFile, List<SAMRecord> samRecords) {
    this.fastaSequenceFile = fastaSequenceFile;
    this.samRecords = samRecords;
    this.variantInfoMap = new HashMap<>();
  }

  /**
   * Method calls variants and transforms a resulting map of objects
   * with variants information - variantInfoMap(which is the class field being filled
   * by the side-effect function - computeContext) to a list of variant contexts
   *
   * @return list of variant contexts which entries hold data about found variants
   * @see #computeContext
   * @see #variantInfoMap
   */
  public List<VariantContext> findVariants() {
    callVariants();
    var result = new ArrayList<VariantContext>();
    variantInfoMap.values().stream()
        .flatMap(contigMap -> contigMap.values().stream())
        .flatMap(positionMap -> positionMap.values().stream())
        .forEach(variantInfo -> {
          VariantContext variantContext = variantInfo.makeVariantContext();
          if (variantContext != null) {
            result.add(variantContext);
          }
        });
    result.sort(Comparator.comparing(VariantContext::getContig)
        .thenComparing(VariantContext::getStart));
    result.forEach(el -> System.out.println(el.toString()));
    return result;
  }

  private void callVariants() {
    ProgressBar progressBar = new ProgressBar(samRecords.size(), System.out);
    samRecords.forEach(samRecord -> {
      processSingleRead(samRecord);
      progressBar.incrementProgress();
    });
  }

  private void processSingleRead(SAMRecord samRecord) {
    if (samRecord.getContig() == null) return;
    String subsequenceBaseString = fastaSequenceFile
        .getSubsequenceAt(samRecord.getContig(), samRecord.getStart(), samRecord.getEnd())
        .getBaseString();
    String readBaseString = samRecord.getReadString();
    String sampleName = samRecord.getAttribute(SAMTag.SM.name()).toString();
    ReadData readData = new ReadData(
        subsequenceBaseString,
        readBaseString,
        sampleName,
        samRecord.getContig(),
        samRecord.getStart(),
        samRecord.getReadNegativeStrandFlag()
    );
    IndexCounter indexCounter = new IndexCounter(0, 0);
    for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {
      processSingleCigarElement(cigarElement, readData, indexCounter);
    }
  }

  private void processSingleCigarElement(
      CigarElement cigarElement,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    // Information about CIGAR operators
    // https://javadoc.io/doc/com.github.samtools/htsjdk/2.13.1/htsjdk/samtools/CigarOperator.html
    CigarOperator operator = cigarElement.getOperator();
    int cigarElementLength = cigarElement.getLength();
    switch (operator) {
      case H:
      case P:
        break;
      case S:
        indexCounter.moveReadIndex(cigarElementLength);
        break;
      case N:
      case I: {
        var alleles = performInsertionOperation(cigarElement.getLength(), readData, indexCounter);
        saveAlleles(alleles, readData, indexCounter.getRefIndex());
        indexCounter.moveReadIndex(cigarElementLength);
        break;
      }
      case D: {
        var alleles = performDeletionOperation(cigarElement.getLength(), readData, indexCounter);
        saveAlleles(alleles, readData, indexCounter.getRefIndex());
        indexCounter.moveRefIndex(cigarElementLength);
        break;
      }
      case M:
      case X:
      case EQ: {
        for (int i = 0; i < cigarElement.getLength(); ++i) {
          var alleles = performAlignmentCigarOperation(readData, indexCounter, i);
          saveAlleles(alleles, readData, indexCounter.getRefIndex() + i);
        }
        indexCounter.moveReadIndex(cigarElementLength);
        indexCounter.moveRefIndex(cigarElementLength);
        break;
      }
    }
  }

  private Alleles performDeletionOperation(
      int cigarElementLength,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    char refChar = readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex());
    Allele refAllele = Allele.create(
        getIndelAlleleString(
            cigarElementLength,
            refChar,
            readData.getReadBaseString(),
            indexCounter.getReadIndex()
        ),
        true
    );
    Allele altAllele = Allele.create(String.valueOf(refChar), false);
    return new Alleles(refAllele, altAllele);
  }

  private Alleles performInsertionOperation(
      int cigarElementLength,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    char refChar = readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex());
    Allele refAllele = Allele.create(String.valueOf(refChar), true);
    Allele altAllele = Allele.create(getIndelAlleleString(
        cigarElementLength,
        refChar,
        readData.getReadBaseString(),
        indexCounter.getReadIndex()
    ), false);
    return new Alleles(refAllele, altAllele);
  }

  private Alleles performAlignmentCigarOperation(
      ReadData readData,
      IndexCounter indexCounter,
      int shift
  ) {
    Allele refAllele = Allele.create(
        String.valueOf(
            readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex() + shift)
        ),
        true
    );
    Allele altAllele = Allele.create(
        String.valueOf(
            readData.getReadBaseString().charAt(indexCounter.getReadIndex() + shift)
        ),
        false
    );
    return new Alleles(refAllele, altAllele);
  }

  private String getIndelAlleleString(int cigarElementLength,
                                      char refChar,
                                      String baseString,
                                      int startIndex
  ) {
    return refChar + baseString.substring(
        startIndex,
        startIndex + cigarElementLength
    );
  }

  /**
   * Method gets a VariantInfo then a SampleData and finally an Allele
   * related to a given variation and increments its count
   * according to a strand flag(so that an allele could be on a forward
   * or a reversed strand)
   *
   * @param alleles  - ref and alt alleles to save
   * @param readData - contains all the read and subsequence information related to one record
   * @param shift    - represents a shift from the start of an aligned read base string
   *                 (used to get a coordinate of a current position at a subsequence)
   * @see Alleles
   * @see ReadData
   * @see #computeContext
   * @see VariantInfo
   * @see SampleData
   * @see Allele
   */
  private void saveAlleles(Alleles alleles, ReadData readData, int shift) {
    computeContext
        (
            readData.getContig(),
            readData.getStart() + shift,
            alleles.getRefAllele()
        )
        .computeSample(readData.getSampleName())
        .computeAllele(alleles.getAltAllele())
        .incrementStrandCount(readData.getReadNegativeStrandFlag());
  }

  /**
   * Method tries to find a context by provided coordinates-parameters
   * and if fails then creates one and puts it into the variantInfoMap
   *
   * @param contig - contig name of a computed allele
   * @param pos    - position at a given contig of a computed allele
   * @param ref    - computed reference allele
   * @return found or created VariantInfo
   * @see VariantInfo
   * @see #variantInfoMap
   */
  private VariantInfo computeContext(String contig, int pos, Allele ref) {
    return Optional.ofNullable(variantInfoMap.get(contig))
        .map(x -> x.get(pos))
        .map(x -> x.get(ref))
        .orElseGet(() -> variantInfoMap
            .computeIfAbsent(contig, key -> new HashMap<>())
            .computeIfAbsent(pos, key -> new HashMap<>())
            .computeIfAbsent(ref, key -> new VariantInfo(contig, pos, ref)));
  }
}
