import com.epam.bioinf.variantcaller.caller.position.PositionStatistics;
import com.epam.bioinf.variantcaller.caller.sample.SampleMetrics;
import htsjdk.variant.variantcontext.Allele;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleMetricsTest {

  @Test
  public void sampleMetricsMustReturnCorrectDp() {
    assertEquals(getCorrectSampleMetrics().getDp(), 20);
  }

  @Test
  public void sampleMetricsMustReturnCorrectDp4() {
    assertEquals(getCorrectSampleMetrics().getDp4(), Arrays.asList(10, 0, 0, 10));
  }

  @Test
  public void sampleMetricsMustReturnCorrectAverageMappingQuality() {
    assertEquals(getCorrectSampleMetrics().getAverageMappingQuality(), 11);
  }

  @Test
  public void sampleMetricsMustReturnCorrectAlleleCount() {
    Map<Allele, Integer> expectedAlleleCnt = new HashMap<>();
    expectedAlleleCnt.put(Allele.create(
        "A",
        false
    ), 10);
    expectedAlleleCnt.put(Allele.create(
        "G",
        true
    ), 10);
    assertEquals(getCorrectSampleMetrics().getAlleleCnt(), expectedAlleleCnt);
  }

  @Test
  public void sampleMetricsMustReturnCorrectFischerExactTestP() {
    assertEquals(getCorrectSampleMetrics().getFischerExactTestP(), 0.0);
  }

  @Test
  public void sampleMetricsMustReturnCorrectBaseQsTtestP() {
    assertEquals(getCorrectSampleMetrics().getBaseQsTtestP(), 0.999);
  }

  @Test
  public void sampleMetricsMustReturnCorrectMapQsTtestP() {
    assertEquals(getCorrectSampleMetrics().getMapQsTtestP(), 0.87);
  }

  @Test
  public void sampleMetricsMustHandleBaseQsAndMapQsEqualValuesScenario() {
    SampleMetrics sampleMetrics = getSampleMetricsWithEqualBaseQsAndMapQs();
    assertEquals(sampleMetrics.getMapQsTtestP(), Double.NaN);
    assertEquals(sampleMetrics.getBaseQsTtestP(), Double.NaN);
  }

  @Test
  public void sampleMetricsMustCorrectlyHandleEmptyAlleleMapScenario() {
    SampleMetrics sampleMetrics = getInvalidSampleMetrics();
    assertEquals(sampleMetrics.getDp(), 0);
    assertEquals(sampleMetrics.getDp4(), Arrays.asList(0, 0, 0, 0));
    assertEquals(sampleMetrics.getAverageMappingQuality(), 0);
    assertEquals(sampleMetrics.getAlleleCnt(), new HashMap<>());
    assertEquals(sampleMetrics.getFischerExactTestP(), 1.0);
    assertEquals(sampleMetrics.getBaseQsTtestP(), Double.NaN);
    assertEquals(sampleMetrics.getMapQsTtestP(), Double.NaN);
  }

  private SampleMetrics getInvalidSampleMetrics() {
    return new SampleMetrics(new HashMap<>());
  }

  private SampleMetrics getSampleMetricsWithEqualBaseQsAndMapQs() {
    Map<Allele, PositionStatistics> alleleMap = new HashMap<>();
    Allele allele1 = Allele.create(
        "A",
        false
    );
    PositionStatistics positionStatistics1 = new PositionStatistics();
    Allele allele2 = Allele.create(
        "G",
        true
    );
    PositionStatistics positionStatistics2 = new PositionStatistics();
    for (int i = 0; i < 10; i++) {
      positionStatistics1.incrementStrandCount(true);
      positionStatistics1.addBaseQ(10);
      positionStatistics1.addMapQ(10);
      positionStatistics2.incrementStrandCount(false);
      positionStatistics2.addBaseQ(10);
      positionStatistics2.addMapQ(10);
    }
    alleleMap.put(allele1, positionStatistics1);
    alleleMap.put(allele2, positionStatistics2);
    return new SampleMetrics(alleleMap);
  }

  private SampleMetrics getCorrectSampleMetrics() {
    Map<Allele, PositionStatistics> alleleMap = new HashMap<>();
    Allele allele1 = Allele.create(
        "A",
        false
    );
    PositionStatistics positionStatistics1 = new PositionStatistics();
    Allele allele2 = Allele.create(
        "G",
        true
    );
    PositionStatistics positionStatistics2 = new PositionStatistics();
    for (int i = 0; i < 10; i++) {
      positionStatistics1.incrementStrandCount(true);
      positionStatistics1.addBaseQ(i);
      positionStatistics1.addMapQ(i * 2);
      positionStatistics2.incrementStrandCount(false);
      positionStatistics2.addBaseQ(i * 5);
      positionStatistics2.addMapQ(i * 3);
    }
    alleleMap.put(allele1, positionStatistics1);
    alleleMap.put(allele2, positionStatistics2);
    return new SampleMetrics(alleleMap);
  }
}
