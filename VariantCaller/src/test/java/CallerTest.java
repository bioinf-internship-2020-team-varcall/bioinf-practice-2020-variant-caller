import org.junit.jupiter.api.Test;

public class CallerTest {
  @Test
  public void variantContextReferenceAlleleMustMatchAccordingSequenceAllele() {
    //TODO Check if a reference allele from a variant context
    // is matching the same allele at the reference sequence
    // (position or 'locus' of an allele at the reference sequence is defined
    // in VariantContext object
  }

  @Test
  public void foundContextMustReturnCorrectAcAndAf() {
    //TODO Check if the found context holds a correct AC and AF
  }

  @Test
  public void foundContextMustReturnCorrectAltAlleles() {
    //TODO Check if the found context holds a correct list of alternate alleles
  }

  @Test
  public void foundContextMustHoldCorrectTotalNumberOfAlleles() {
    //TODO Check if the found context holds a correct total number of alleles at the given locus
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectSampleNames() {
    //TODO Check if the found context holds genotypes
    // with correct names of a read group(SM или sample name в @RG)
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectListOfAllelesAndDpg() {
    //TODO Check if a genotype in a found context holds correct list of
    // alleles and the given locus and genotype DPG is matching this list
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectDp() {
    //TODO Check if a genotype in a found context holds correct DP
  }
}
