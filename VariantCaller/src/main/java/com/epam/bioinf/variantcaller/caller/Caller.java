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

  public ArrayList<Variant> call(IndexedFastaSequenceFile fastaSequenceFile,
      List<SAMRecord> samRecords) {
    this.fastaSequenceFile = fastaSequenceFile;
    this.samRecords = samRecords;
    initVariants();
    ArrayList<Variant> variants = new ArrayList<>();
    this.variants.keySet().forEach(contigKey -> {
      HashMap potentialVariants = this.variants.get(contigKey);
      this.variants.get(contigKey).forEach((posKey, potentialVariant) -> {
        variants.add(new Variant(contigKey, posKey, potentialVariant.getVariants(),
            potentialVariant.getRefAllele()));
      });
    });
    return variants;
  }

  private void initVariants() {
    samRecords.forEach(samRecord -> {
      String contig = samRecord.getContig();
      if (contig != null) {

        variants.compute(contig, (contigKey , variantsByContig) -> {
          if (variantsByContig == null) {
            variantsByContig = new HashMap<Integer, PotentialVariants>();
          }

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

                variantsByContig.compute(readInd, (posKey, potentialVariant) -> {
                  byte c = readBases[constReadInd];
                  if (potentialVariant == null) {
                    potentialVariant = new PotentialVariants(subsequenceBases[constReadInd]);
                  }
                  if (potentialVariant.getRefChar() != c) {
                    potentialVariant.addPotentialVariant(c);
                  }
                  return potentialVariant;
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
          return variantsByContig;
        });
      }
    });
  }
}
