package com.epam.bioinf.variantcaller.handlers;

import com.epam.bioinf.variantcaller.cmdline.ParsedArguments;
import com.epam.bioinf.variantcaller.exceptions.handlers.fasta.FastaMultipleSequencesException;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.*;

import java.io.IOException;

/**
 * Class holds a sequence and performs work with it.
 * This implementation is temporary and will be changed in future versions.
 */
public class FastaHandler {
  private IndexedFastaSequenceFile fastaSequenceFile;

  /**
   * Constructor
   */
  public FastaHandler(ParsedArguments parsedArguments) {
    try {
      FastaSequenceIndex fastaSequenceIndex = FastaSequenceIndexCreator
          .buildFromFasta(parsedArguments.getFastaPath());
      fastaSequenceFile = new IndexedFastaSequenceFile(
          parsedArguments.getFastaPath(), fastaSequenceIndex);
    } catch (IOException e) {
      e.printStackTrace(); // Handled by ParsedArguments
    }
  }

  /**
   * Return sequence at specified contig and range
   */
  public ReferenceSequence getSubsequence(String contig, long start, long stop) {
    return fastaSequenceFile.getSubsequenceAt(contig, start, stop);
  }

  /**
   * Method for testing class
   */
  public IndexedFastaSequenceFile getFastaSequenceFile() {
    return fastaSequenceFile;
  }
}
