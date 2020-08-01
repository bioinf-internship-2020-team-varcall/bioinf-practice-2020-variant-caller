package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.helpers.ProgressBar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static htsjdk.samtools.CigarOperator.D;
import static htsjdk.samtools.CigarOperator.I;

public class Caller {
  private final IndexedFastaSequenceFile fastaSequenceFile;
  private final List<SAMRecord> samRecords;
  private final List<VariantInfo> variantInfoList;

  public Caller(IndexedFastaSequenceFile fastaSequenceFile, List<SAMRecord> samRecords) {
    this.fastaSequenceFile = fastaSequenceFile;
    this.samRecords = samRecords;
    this.variantInfoList = new ArrayList<>();
  }

  public List<VariantContext> findVariants() {
    callVariants();
    var result = variantInfoList
        .stream()
        .map(VariantInfo::makeVariantContext)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
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
    for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {
      processSingleCigarElement(cigarElement, readData);
    }
  }

  private void processSingleCigarElement(CigarElement cigarElement, ReadData readData) {
    IndexCounter indexCounter = new IndexCounter(0, 0);
    // Information about CIGAR operators
    // https://javadoc.io/doc/com.github.samtools/htsjdk/2.13.1/htsjdk/samtools/CigarOperator.html
    CigarOperator operator = cigarElement.getOperator();
    int cigarElementLength = cigarElement.getLength();
    switch (operator) {
      case H:
      case P:
        break;
      case S:
        indexCounter.moveReadIndex(cigarElementLength - 1);
        break;
      case N:
      case I: {
        performIndelCigarOperation(cigarElement, readData, indexCounter);
        indexCounter.moveReadIndex(cigarElementLength - 1);
        break;
      }
      case D: {
        performIndelCigarOperation(cigarElement, readData, indexCounter);
        indexCounter.moveRefIndex(cigarElementLength - 1);
        break;
      }
      case M:
      case X:
      case EQ: {
        performAlignmentCigarOperation(cigarElement, readData, indexCounter);
        indexCounter.moveReadIndex(cigarElementLength - 1);
        indexCounter.moveRefIndex(cigarElementLength - 1);
        break;
      }
    }
  }

  private void performIndelCigarOperation(
      CigarElement cigarElement,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    char refChar = readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex());
    Allele refAllele = getRefAlleleForIndel(
        cigarElement,
        refChar,
        readData.getSubsequenceBaseString(),
        indexCounter
    );
    Allele altAllele = getAltAlleleForIndel(
        cigarElement,
        refChar,
        readData.getReadBaseString(),
        indexCounter
    );
    if (refAllele != null && altAllele != null) {
      computeContext
          (
              readData.getContig(),
              readData.getStart() + indexCounter.getRefIndex(),
              refAllele
          )
          .computeSample(readData.getSampleName())
          .computeAllele(altAllele).
          incrementStrandCount(readData.getReadNegativeStrandFlag());
    }
  }

  private void performAlignmentCigarOperation(
      CigarElement cigarElement,
      ReadData readData,
      IndexCounter indexCounter
  ) {
    for (int i = 0; i < cigarElement.getLength(); ++i) {
      computeContext
          (
              readData.getContig(),
              readData.getStart() + indexCounter.getRefIndex() + i,
              getRefAlleleForAlignment(readData, indexCounter, i)
          )
          .computeSample(readData.getSampleName())
          .computeAllele(getAltAlleleForAlignment(readData, indexCounter, i))
          .incrementStrandCount(readData.getReadNegativeStrandFlag());
    }
  }

  private Allele getRefAlleleForAlignment(ReadData readData, IndexCounter indexCounter, int shift) {
    return Allele.create(
        String.valueOf(
            readData.getSubsequenceBaseString().charAt(indexCounter.getRefIndex() + shift)
        ),
        true
    );
  }

  private Allele getAltAlleleForAlignment(ReadData readData, IndexCounter indexCounter, int shift) {
    return Allele.create(
        String.valueOf(
            readData.getReadBaseString().charAt(indexCounter.getReadIndex() + shift)
        ),
        false
    );
  }

  private Allele getRefAlleleForIndel(
      CigarElement cigarElement,
      char refChar,
      String subsequenceBaseString,
      IndexCounter indexCounter
  ) {
    if (cigarElement.getOperator() == I) {
      return Allele.create(String.valueOf(refChar), true);
    } else if (cigarElement.getOperator() == D) {
      String alleleString = refChar + subsequenceBaseString.substring(
          indexCounter.getRefIndex(),
          indexCounter.getRefIndex() + cigarElement.getLength()
      );
      return Allele.create(alleleString, true);
    }
    return null;
  }

  private Allele getAltAlleleForIndel(
      CigarElement cigarElement,
      char refChar,
      String readBaseString,
      IndexCounter indexCounter
  ) {
    if (cigarElement.getOperator() == I) {
      String alleleString = refChar + readBaseString.substring(
          indexCounter.getReadIndex(),
          indexCounter.getReadIndex() + cigarElement.getLength()
      );
      return Allele.create(alleleString, false);
    } else if (cigarElement.getOperator() == D) {
      return Allele.create(String.valueOf(refChar), false);
    }
    return null;
  }

  private VariantInfo computeContext(String contig, int pos, Allele ref) {
    Optional<VariantInfo> context = variantInfoList
        .stream()
        .filter(
            ctx -> ctx.getContig().equals(contig)
                && ctx.getPos() == pos
                && ctx.getRefAllele().compareTo(ref) == 0
        )
        .findFirst();
    if (context.isPresent()) {
      return context.get();
    } else {
      VariantInfo variantInfo = new VariantInfo(contig, pos, ref);
      variantInfoList.add(variantInfo);
      return variantInfo;
    }
  }
}
