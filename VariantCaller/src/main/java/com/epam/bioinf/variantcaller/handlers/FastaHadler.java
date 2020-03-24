package com.epam.bioinf.variantcaller.handlers;

import htsjdk.samtools.reference.FastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

import java.nio.file.Path;

import static com.epam.bioinf.variantcaller.handlers.FastaHadler.FastaHadlerMessages.MULTIPLE_SEQUENCES_EXC;

public class FastaHadler {
  private final ReferenceSequence sequence;

  public FastaHadler(Path pathToFastaFile) {
    FastaSequenceFile fastaSequenceFile = new FastaSequenceFile(pathToFastaFile, true);
    sequence = fastaSequenceFile.nextSequence();
    if (fastaSequenceFile.nextSequence() != null)
      throw new IllegalArgumentException(MULTIPLE_SEQUENCES_EXC);
    fastaSequenceFile.close();
  }

  public double getGcContent() {
    String s = sequence.getBaseString();
    return (double) s
        .chars()
        .filter(c -> c == 'G' || c == 'C')
        .mapToObj(i -> (char) i)
        .count() / (double) s.length() * 100;
  }

  public int countNucleotides() {
    return sequence.getBaseString().length();
  }

  public static final class FastaHadlerMessages {
    private FastaHadlerMessages() {
      // restrict instantiation
    }

    public static final String MULTIPLE_SEQUENCES_EXC =
        "Multiple fasta sequences were provided, fasta file must contain only one sequence";
  }
}
