package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import htsjdk.samtools.reference.FastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

import java.nio.file.Path;

import static com.epam.bioinf.variantcaller.helpers.exceptions.messages.FastaHandlerMessages.*;

/**
 * Class holds a sequence and performs work with it
 */
public class FastaHadler {
  private final ReferenceSequence sequence;

  /**
   * Costructor checks if a provided file has only one sequence and if true holds this sequence
   */
  public FastaHadler(ParsedArguments parsedArguments) {
    FastaSequenceFile fastaSequenceFile =
        new FastaSequenceFile(parsedArguments.getFastaPath(), true);
    sequence = fastaSequenceFile.nextSequence();
    if (fastaSequenceFile.nextSequence() != null) {
      throw new IllegalArgumentException(MULTIPLE_SEQUENCES_EXC);
    }
  }

  /**
   * Method counts GC content in a sequence
   */
  public double getGcContent() {
    String s = sequence.getBaseString();
    return s
        .chars()
        .filter(c -> c == 'G' || c == 'C')
        .count() * 1.0 / s.length() * 100.0;
  }

  /**
   * Method counts nucleotides in a sequence
   */
  public int countNucleotides() {
    return sequence.getBaseString().length();
  }
}
