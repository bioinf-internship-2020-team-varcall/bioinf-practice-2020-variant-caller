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

public class Caller {
  private final IndexedFastaSequenceFile fastaSequenceFile;
  private final List<SAMRecord> samRecords;
  private final Map<String, Map<Integer, Map<Allele, VariantInfo>>> variantInfoMap;

  public Caller(IndexedFastaSequenceFile fastaSequenceFile, List<SAMRecord> samRecords) {
    this.fastaSequenceFile = fastaSequenceFile;
    this.samRecords = samRecords;
    this.variantInfoMap = new HashMap<>();
  }

  public List<VariantContext> findVariants() {
    callVariants();
    var result = new ArrayList<VariantContext>();
    variantInfoMap.keySet().forEach(contig -> {
      variantInfoMap.get(contig).keySet().forEach(pos -> {
        variantInfoMap.get(contig).get(pos).keySet().forEach(allele -> {
          VariantInfo variantInfo = variantInfoMap.get(contig).get(pos).get(allele);
          if (variantInfo != null) {
            VariantContext variantContext = variantInfo.makeVariantContext();
            if (variantContext != null) {
              result.add(variantContext);
            }
          }
        });
      });
    });
    result.sort(Comparator.comparing(VariantContext::getContig).thenComparing(VariantContext::getStart));
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
        performInsertionOperation(cigarElement, readData, indexCounter);
        indexCounter.moveReadIndex(cigarElementLength);
        break;
      }
      case D: {
        performDeletionOperation(cigarElement, readData, indexCounter);
        indexCounter.moveRefIndex(cigarElementLength);
        break;
      }
      case M:
      case X:
      case EQ: {
        performAlignmentCigarOperation(cigarElement, readData, indexCounter);
        indexCounter.moveReadIndex(cigarElementLength);
        indexCounter.moveRefIndex(cigarElementLength);
        break;
      }
    }
  }

  private void performDeletionOperation(
      CigarElement cigarElement,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    char refChar = readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex());
    Allele refAllele = Allele.create(
        getIndelAlleleString(
            cigarElement.getLength(),
            refChar,
            readData.getReadBaseString(),
            indexCounter.getReadIndex()
        ),
        true
    );
    Allele altAllele = Allele.create(String.valueOf(refChar), false);
    if (refAllele != null && altAllele != null) {
      saveAlleles(refAllele, altAllele, readData, indexCounter.getRefIndex());
    }
  }

  private void performInsertionOperation(
      CigarElement cigarElement,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    char refChar = readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex());
    Allele refAllele = Allele.create(String.valueOf(refChar), true);
    Allele altAllele = Allele.create(getIndelAlleleString(
        cigarElement.getLength(),
        refChar,
        readData.getReadBaseString(),
        indexCounter.getReadIndex()
    ), false);
    if (refAllele != null && altAllele != null) {
      saveAlleles(refAllele, altAllele, readData, indexCounter.getRefIndex());
    }
  }

  private void performAlignmentCigarOperation(
      CigarElement cigarElement,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    for (int i = 0; i < cigarElement.getLength(); ++i) {
      Allele refAllele = Allele.create(
          String.valueOf(
              readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex() + i)
          ),
          true
      );
      Allele altAllele = Allele.create(
          String.valueOf(
              readData.getReadBaseString().charAt(indexCounter.getReadIndex() + i)
          ),
          false
      );
      saveAlleles(refAllele, altAllele, readData, indexCounter.getRefIndex() + i);
    }
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

  private void saveAlleles(Allele refAllele, Allele altAllele, ReadData readData, int shift) {
    computeContext
        (
            readData.getContig(),
            readData.getStart() + shift,
            refAllele
        )
        .computeSample(readData.getSampleName())
        .computeAllele(altAllele)
        .incrementStrandCount(readData.getReadNegativeStrandFlag());
  }

  private VariantInfo computeContext(String contig, int pos, Allele ref) {
    return Optional.ofNullable(variantInfoMap.get(contig))
        .map(x -> x.get(pos))
        .map(x -> x.get(ref))
        .orElseGet(() -> {
            VariantInfo variantInfo = new VariantInfo(contig, pos, ref);
            variantInfoMap.putIfAbsent(contig, new HashMap<>());
            variantInfoMap.get(contig).putIfAbsent(pos, new HashMap<>());
            variantInfoMap.get(contig).get(pos).putIfAbsent(ref, variantInfo);
            return variantInfo;
        });
  }
}
