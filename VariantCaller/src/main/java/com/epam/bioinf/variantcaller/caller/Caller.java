package com.epam.bioinf.variantcaller.caller;

import com.epam.bioinf.variantcaller.helpers.ProgressBar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

import java.util.*;

public class Caller {
  private IndexedFastaSequenceFile fastaSequenceFile;
  private List<SAMRecord> samRecords;
  private final HashMap<String, HashMap<Integer, PotentialVariants>> variants;

  public Caller(IndexedFastaSequenceFile fastaSequenceFile,
      List<SAMRecord> samRecords) {
    variants = new HashMap<>();
    this.fastaSequenceFile = fastaSequenceFile;
    this.samRecords = samRecords;
  }

  public ArrayList<Variant> call() {
    initVariants();
    ArrayList<Variant> variants = new ArrayList<>();
    this.variants.forEach((contigKey, contigValue) -> {
      contigValue.forEach((posKey, potentialVariant) -> {
        if (potentialVariant.getVariants().isEmpty()) {
          variants.add(new Variant(contigKey, posKey, potentialVariant.getVariants(),
              potentialVariant.getRefAllele()));
        }
      });
    });
    return variants;
  }

  private void initVariants() {
    ProgressBar progressBar = new ProgressBar(samRecords.size());
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
                int constRefInd = refInd;
                int trueInd = start + refInd;

                variantsByContig.compute(trueInd, (posKey, potentialVariant) -> {
                  byte c = readBases[constReadInd];
                  if (potentialVariant == null) {
                    byte refChar = subsequenceBases[constRefInd];
                    if (refChar != c) {
                      potentialVariant = new PotentialVariants(refChar);
                      potentialVariant.addPotentialVariant(c);
                      return potentialVariant;
                    } else {
                      return null;
                    }
                  } else {
                    if (potentialVariant.getRefChar() != c) {
                      potentialVariant.addPotentialVariant(c);
                    }
                    return potentialVariant;
                  }
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
      progressBar.process();
    });
  }
}
