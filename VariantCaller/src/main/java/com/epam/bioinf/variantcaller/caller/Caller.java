package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.caller.position.PositionTracker;
import com.epam.bioinf.variantcaller.caller.variant.VariantInfo;
import com.epam.bioinf.variantcaller.helpers.ProgressBar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.*;

/**
 * Finds and holds variants.
 */
public class Caller {
  private final IndexedFastaSequenceFile fastaSequenceFile;
  private final List<SAMRecord> samRecords;
  private final Map<String, Map<Integer, Map<Allele, VariantInfo>>> variantInfoMap;

  /**
   * Gets an indexed fasta sequence file
   * and a list of sam records matching the given intervals.
   */
  public Caller(IndexedFastaSequenceFile fastaSequenceFile, List<SAMRecord> samRecords) {
    this.fastaSequenceFile = fastaSequenceFile;
    this.samRecords = samRecords;
    this.variantInfoMap = new HashMap<>();
  }

  /**
   * Iterates over the list of {@link samRecords} and returns variants found.
   *
   * @return list of variant contexts which entries hold data about found variants
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
    ReadData readData = new ReadData(
        subsequenceBaseString,
        samRecord
    );
    PositionTracker positionTracker = new PositionTracker(0, 0);
    for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {
      processSingleCigarElement(cigarElement, readData, positionTracker);
    }
  }

  private void processSingleCigarElement(
      CigarElement cigarElement,
      ReadData readData,
      PositionTracker positionTracker
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
        positionTracker.moveIndices(0, cigarElementLength);
        break;
      case N:
      case I: {
        var alleles = performInsertionOperation(
            cigarElement.getLength(),
            readData,
            positionTracker
        );
        saveAlleles(alleles, readData, positionTracker.getRefIndex());
        positionTracker.moveIndices(0, cigarElementLength);
        break;
      }
      case D: {
        var alleles = performDeletionOperation(
            cigarElement.getLength(),
            readData,
            positionTracker
        );
        saveAlleles(alleles, readData, positionTracker.getRefIndex());
        positionTracker.moveIndices(cigarElementLength, 0);
        break;
      }
      case M:
      case X:
      case EQ: {
        for (int i = 0; i < cigarElement.getLength(); ++i) {
          var alleles = performAlignmentCigarOperation(readData, positionTracker, i);
          saveAlleles(alleles, readData, positionTracker.getRefIndex() + i);
        }
        positionTracker.moveIndices(cigarElementLength, cigarElementLength);
        break;
      }
    }
  }

  private Alleles performDeletionOperation(
      int cigarElementLength,
      ReadData readData,
      PositionTracker positionTracker
  ) {
    char refChar = readData.getSubsequenceBaseString().charAt(positionTracker.getRefIndex());
    Allele refAllele = Allele.create(
        getIndelAlleleString(
            cigarElementLength,
            refChar,
            readData.getReadBaseString(),
            positionTracker.getReadIndex()
        ),
        true
    );
    Allele altAllele = Allele.create(String.valueOf(refChar), false);
    return new Alleles(refAllele, altAllele);
  }

  private Alleles performInsertionOperation(
      int cigarElementLength,
      ReadData readData,
      PositionTracker positionTracker
  ) {
    char refChar = readData.getSubsequenceBaseString().charAt(positionTracker.getRefIndex());
    Allele refAllele = Allele.create(String.valueOf(refChar), true);
    Allele altAllele = Allele.create(getIndelAlleleString(
        cigarElementLength,
        refChar,
        readData.getReadBaseString(),
        positionTracker.getReadIndex()
    ), false);
    return new Alleles(refAllele, altAllele);
  }

  private Alleles performAlignmentCigarOperation(
      ReadData readData,
      PositionTracker positionTracker,
      int shift
  ) {
    Allele refAllele = Allele.create(
        String.valueOf(
            readData.getSubsequenceBaseString().charAt(positionTracker.getRefIndex() + shift)
        ),
        true
    );
    Allele altAllele = Allele.create(
        String.valueOf(
            readData.getReadBaseString().charAt(positionTracker.getReadIndex() + shift)
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
   * Increments alleles count.
   *
   * @param alleles  - ref and alt alleles to save
   * @param readData - contains all the read and subsequence information related to one record
   * @param shift    - represents a shift from the start of an aligned read base string
   *                 (used to get a coordinate of a current position at a subsequence)
   * @see Alleles
   * @see ReadData
   */
  private void saveAlleles(Alleles alleles, ReadData readData, int shift) {
    computeVariantInfo
        (
            readData.getContig(),
            readData.getStart() + shift,
            alleles.getRefAllele()
        )
        .computeSample(readData.getSampleName())
        .computeAllele(alleles.getAltAllele())
        .incrementStrandCount(readData.getReadNegativeStrandFlag())
        .addMapQ(readData.getMappingQuality())
        .addBaseQ(readData.getBaseQualityAtPosition(shift));
  }

  /**
   * Finds a {@link VariantInfo} by provided coordinates-parameters,
   * if it is not found, creates one and puts it.
   *
   * @param contig - contig name of a computed allele
   * @param pos    - position at a given contig of a computed allele
   * @param ref    - computed reference allele
   * @return found or created VariantInfo
   */
  private VariantInfo computeVariantInfo(String contig, int pos, Allele ref) {
    return Optional.ofNullable(variantInfoMap.get(contig))
        .map(x -> x.get(pos))
        .map(x -> x.get(ref))
        .orElseGet(() -> variantInfoMap
            .computeIfAbsent(contig, key -> new HashMap<>())
            .computeIfAbsent(pos, key -> new HashMap<>())
            .computeIfAbsent(ref, key -> new VariantInfo(contig, pos, ref)));
  }
}
