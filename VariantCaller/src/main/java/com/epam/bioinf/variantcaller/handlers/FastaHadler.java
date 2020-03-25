package com.epam.bioinf.variantcaller.handlers;

import htsjdk.samtools.reference.FastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

import java.nio.file.Path;

import static com.epam.bioinf.variantcaller.handlers.FastaHadler.FastaHadlerMessages.*;

/**
 * Class holds a sequence and performs work with it
 */
public class FastaHadler {
  private final ReferenceSequence sequence;

  /**
   * Costructor checks if a provided file has only one sequence and if true holds this sequence
   */
  public FastaHadler(Path pathToFastaFile) {
    FastaSequenceFile fastaSequenceFile = new FastaSequenceFile(pathToFastaFile, true);
    sequence = fastaSequenceFile.nextSequence();
    if (fastaSequenceFile.nextSequence() != null)
      throw new IllegalArgumentException(MULTIPLE_SEQUENCES_EXC);
  }

  /**
   * Method counts GC content in a sequence
   */
  public double getGcContent() {
    String s = sequence.getBaseString();
    return (double) s
        .chars()
        .filter(c -> c == 'G' || c == 'C')
        .count() / s.length() * 100.0;
  }

  /**
   * Method counts nucleotides in a sequence
   */
  public int countNucleotides() {
    return sequence.getBaseString().length();
  }

  /**
   * Class holds exception messages for FastaHandler
   */
  public static final class FastaHadlerMessages {
    private FastaHadlerMessages() {
      // restrict instantiation
    }

    public static final String MULTIPLE_SEQUENCES_EXC =
        "Multiple fasta sequences were provided, fasta file must contain only one sequence";
  }
}
