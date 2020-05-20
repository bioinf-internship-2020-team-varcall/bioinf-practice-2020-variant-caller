package com.epam.bioinf.variantcaller.caller;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import org.apache.commons.compress.utils.Sets;

import java.util.*;

public class Caller {
  private IndexedFastaSequenceFile fastaSequenceFile;
  private List<SAMRecord> samRecords;
  private final HashMap<String, HashMap<Integer, PotentialVariants>> variants;

  public Caller() {
    variants = new HashMap<>();
  }

  public void call(IndexedFastaSequenceFile fastaSequenceFile, List<SAMRecord> samRecords) {
    this.fastaSequenceFile = fastaSequenceFile;
    this.samRecords = samRecords;
    initVariants();
  }

  private void initVariants() {
    samRecords.forEach(samRecord -> {
      String contig = samRecord.getContig();
      if (contig != null) {
        if (!variants.containsKey(contig)) {
          variants.put(contig,
              new HashMap<Integer, PotentialVariants>());
        }

        HashMap<Integer, PotentialVariants> variantsByContig = variants.get(contig);
        int start = samRecord.getStart();
        ReferenceSequence subsequenceAt =
            fastaSequenceFile.getSubsequenceAt(contig, start, samRecord.getEnd());
        byte[] subsequenceBases = subsequenceAt.getBases();

        int refInd = 0;
        int readInd = 0;
        byte[] readBases = samRecord.getReadBases();
        for (CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {
          CigarOperator operator = cigarElement.getOperator();
          int length = cigarElement.getLength();
          if (operator.isAlignment()) {
            for (int j = 0; j < length - 1; j++) {

              int constReadInd = readInd;

              variantsByContig.compute(readInd, (key, val) -> {
                byte c = readBases[constReadInd];
                if (val == null) {
                  val = new PotentialVariants(subsequenceBases[constReadInd]);
                }
                if (val.getRefChar() != c) {
                  val.addPotentialVariant(c);
                }
                return val;
              });

              refInd++;
              readInd++;
            }
          } else if (operator.equals(CigarOperator.D)) {
            refInd += length + 1;
            readInd++;
          } else if (operator.equals(CigarOperator.I)) {
            readInd += length + 1;
            refInd++;
          }
        }
        //variants.computeIfPresent(contig, (key, value) -> value = variantsByContig);
        variants.put(contig, variantsByContig);
      }
    });
  }
}
