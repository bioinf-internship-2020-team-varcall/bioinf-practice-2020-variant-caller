package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.helpers.ProgressBar;
import htsjdk.samtools.*;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
    for (SAMRecord samRecord : samRecords) {
      String contig = samRecord.getContig();
      if (contig == null) {
        continue;
      }
      ReferenceSequence subsequenceAt =
          fastaSequenceFile.getSubsequenceAt(contig, samRecord.getStart(), samRecord.getEnd());
      byte[] subsequenceBases = subsequenceAt.getBases();
      processSingleRead(samRecord, subsequenceBases);
      progressBar.incrementProgress();
    }
  }

  private void processSingleRead(
      SAMRecord samRecord,
      byte[] subsequenceBases) {
    String sampleName = samRecord.getAttribute(SAMTag.SM.name()).toString();
    int refInd = 0;
    int readInd = 0;
    byte[] readBases = samRecord.getReadBases();
    for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {
      //Information about CIGAR operators - http://https://drive5.com/usearch/manual/cigar.html
      CigarOperator operator = cigarElement.getOperator();
      int length = cigarElement.getLength();
      byte refByte = subsequenceBases[refInd];
      switch (operator) {
        case H:
        case P:
          break;
        case S:
          readInd += length;
          break;
        case N:
        case I: {
          if (refInd > 0) {
            String inserted = byteToString(refByte) +
                new String(Arrays.copyOfRange(readBases, readInd, readInd + length),
                    StandardCharsets.UTF_8);
            findContext(samRecord.getContig(),
                samRecord.getStart() + refInd,
                Allele.create(byteToString(refByte), true))
                .getSample(sampleName)
                .getAllele(Allele.create(inserted, false)).
                incrementStrandCount(samRecord.getReadNegativeStrandFlag());
          }
          readInd += length - 1;
          break;
        }
        case D: {
          if (refInd > 0) {
            String deleted = byteToString(refByte) +
                new String(Arrays.copyOfRange(subsequenceBases, refInd, refInd + length),
                    StandardCharsets.UTF_8);
            findContext(samRecord.getContig(),
                samRecord.getStart() + refInd,
                Allele.create(deleted, true))
                .getSample(sampleName)
                .getAllele(Allele.create(byteToString(refByte), false)).
                incrementStrandCount(samRecord.getReadNegativeStrandFlag());
          }
          refInd += length - 1;
          break;
        }
        case M:
        case X:
        case EQ: {
          for (int i = 0; i < length; ++i) {
            findContext(samRecord.getContig(),
                samRecord.getStart() + refInd + i,
                Allele.create(subsequenceBases[refInd + i], true))
                .getSample(sampleName)
                .getAllele(Allele.create(byteToString(readBases[readInd + i]), false))
                .incrementStrandCount(samRecord.getReadNegativeStrandFlag());
          }
          readInd += length - 1;
          refInd += length - 1;
          break;
        }
      }
    }
  }

  private String byteToString(byte b) {
    return String.valueOf((char) (b & 0xFF));
  }

  private VariantInfo findContext(String contig, int pos, Allele ref) {
    Optional<VariantInfo> context = variantInfoList
        .stream()
        .filter(ctx -> ctx.getContig().equals(contig) && ctx.getPos() == pos)
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
