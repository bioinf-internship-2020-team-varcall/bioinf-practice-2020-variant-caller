package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.helpers.ProgressBar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.nio.charset.StandardCharsets;
import java.util.*;
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
    byte[] subsequenceBases = fastaSequenceFile
        .getSubsequenceAt(samRecord.getContig(), samRecord.getStart(), samRecord.getEnd())
        .getBases();
    byte[] readBases = samRecord.getReadBases();
    String sampleName = samRecord.getAttribute(SAMTag.SM.name()).toString();
    ReadData readData = new ReadData(
        subsequenceBases,
        readBases,
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
    //Information about CIGAR operators - http://https://drive5.com/usearch/manual/cigar.html
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

  private void performIndelCigarOperation(CigarElement cigarElement, ReadData readData, IndexCounter indexCounter) {
    byte refByte = readData.getSubsequenceBases()[indexCounter.getRefIndex()];
    Allele refAllele = getRefAlleleForIndel(cigarElement, refByte, readData.getSubsequenceBases(), indexCounter);
    Allele altAllele = getAltAlleleForIndel(cigarElement, refByte, readData.getReadBases(), indexCounter);
    if (refAllele != null && altAllele != null) {
      findContext(readData.getContig(), readData.getStart() + indexCounter.getRefIndex(), refAllele)
          .getSample(readData.getSampleName())
          .getAllele(altAllele).
          incrementStrandCount(readData.getReadNegativeStrandFlag());
    }
  }

  private void performAlignmentCigarOperation(CigarElement cigarElement, ReadData readData, IndexCounter indexCounter) {
    for (int i = 0; i < cigarElement.getLength(); ++i) {
      findContext(readData.getContig(), readData.getStart() + indexCounter.getMovedRefIndex(i),
          getRefAlleleForAlignment(readData, indexCounter, i))
          .getSample(readData.getSampleName())
          .getAllele(getAltAlleleForAlignment(readData, indexCounter, i))
          .incrementStrandCount(readData.getReadNegativeStrandFlag());
    }
  }

  private Allele getRefAlleleForAlignment(ReadData readData, IndexCounter indexCounter, int shift) {
    return Allele.create(readData.getSubsequenceBases()[indexCounter.getMovedRefIndex(shift)], true);
  }

  private Allele getAltAlleleForAlignment(ReadData readData, IndexCounter indexCounter, int shift) {
    return Allele.create(byteToString(readData.getReadBases()[indexCounter.getMovedReadIndex(shift)]), false);
  }

  private Allele getRefAlleleForIndel(CigarElement cigarElement, byte refByte, byte[] subsequenceBases, IndexCounter indexCounter) {
    if (cigarElement.getOperator() == I) {
      return Allele.create(byteToString(refByte), true);
    } else if (cigarElement.getOperator() == D) {
      String alleleString = byteToString(refByte) +
          new String(Arrays.copyOfRange(subsequenceBases, indexCounter.getRefIndex(), indexCounter.getMovedRefIndex(cigarElement.getLength())),
              StandardCharsets.UTF_8);
      return Allele.create(alleleString, true);
    }
    return null;
  }

  private Allele getAltAlleleForIndel(CigarElement cigarElement, byte refByte, byte[] readBases, IndexCounter indexCounter) {
    if (cigarElement.getOperator() == I) {
      String alleleString = byteToString(refByte) +
          new String(Arrays.copyOfRange(readBases, indexCounter.getReadIndex(), indexCounter.getMovedReadIndex(cigarElement.getLength())),
              StandardCharsets.UTF_8);
      return Allele.create(alleleString, false);
    } else if (cigarElement.getOperator() == D) {
      return Allele.create(byteToString(refByte), false);
    }
    return null;
  }

  private String byteToString(byte b) {
    return String.valueOf((char) (b & 0xFF));
  }

  private VariantInfo findContext(String contig, int pos, Allele ref) {
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
