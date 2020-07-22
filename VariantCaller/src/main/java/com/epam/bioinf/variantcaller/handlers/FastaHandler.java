package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.handlers.fasta.FastaHandlerUnableToFindEntryException;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.FastaSequenceIndexCreator;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Class holds a reference file with sequences and performs work with it.
 * This implementation may change in future versions.
 */
public class FastaHandler {
  private IndexedFastaSequenceFile fastaSequenceFile;

  /**
   * Constructor creates indexed sequence file and stores it.
   * Unavoidable AsciiLineReader warning is redirected to nullOutputStream
   * Throwable exception is handled by ParsedArguments
   *
   * @param parsedArguments parsedArguments with validated path to fasta file
   * @see ParsedArguments
   */
  public FastaHandler(ParsedArguments parsedArguments) {
    try {
      FastaSequenceIndex fastaSequenceIndex =
          getSequenceIndexFileWithoutWarning(parsedArguments.getFastaPath());
      fastaSequenceFile =
          new IndexedFastaSequenceFile(parsedArguments.getFastaPath(), fastaSequenceIndex);
    } catch (IOException e) { // Handled by ParsedArguments
      e.printStackTrace();
    }
  }

  private FastaSequenceIndex getSequenceIndexFileWithoutWarning(Path fastaPath)
      throws IOException {
    // Temporary redirection of AsciiLineReader warning
    final PrintStream stdErr = System.err;
    System.setErr(new PrintStream(OutputStream.nullOutputStream(), true, "utf-8"));
    FastaSequenceIndex fastaSequenceIndex = FastaSequenceIndexCreator.buildFromFasta(fastaPath);
    System.setErr(stdErr);
    return fastaSequenceIndex;
  }

  /**
   * Return sequence at specified contig and range
   *
   * @param contig reference chromosome
   * @param start  range start index
   * @param stop   range end index
   * @return ReferenceSequence
   */
  public ReferenceSequence getSubsequence(String contig, long start, long stop) {
    try {
      return fastaSequenceFile.getSubsequenceAt(contig, start, stop);
    } catch (SAMException e) {
      throw new FastaHandlerUnableToFindEntryException(e.getMessage());
    }
  }

  /**
   * Method for testing class
   */
  public IndexedFastaSequenceFile getFastaSequenceFile() {
    return fastaSequenceFile;
  }
}
