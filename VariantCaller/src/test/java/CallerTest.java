import org.junit.jupiter.api.Test;

public class CallerTest {
  @Test
  public void variantContextReferenceAlleleMustMatchAccordingSequenceAllele() {
    //TODO Проверить, что референс аллель
    // в полученном коллером вариант контексте соответствует тому же референс аллелю
    // на указанной в контексте позиции на референс последовательности
  }

  @Test
  public void foundContextMustReturnCorrectAcAndAf() {
    //TODO Проверить, что найденный контекст содержит корректный AC и AF
  }

  @Test
  public void foundContextMustReturnCorrectAltAlleles() {
    //TODO Проверить, что найденный контекст содержит корректные альтернативные аллели
  }

  @Test
  public void foundContextMustHoldCorrectTotalNumberOfAlleles() {
    //TODO Проверить, что найденный контекст содержит корректное общее количество аллелей
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectSampleNames() {
    //TODO Проверить, что найденный контекст содержит генотипы
    // с корректными именами read group(SM или sample name в @RG)
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectListOfAllelesAndDpg() {
    //TODO Проверить, что генотип в найденном контексте содержит
    // корректный список аллелей для этой read group, а также соотвествующие DPG
  }

  @Test
  public void foundContextMustHoldGenotypesWithCorrectDp() {
    //TODO Проверить, что генотип в найденном контексте содержит корректный DP
  }
}
