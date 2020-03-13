import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

@CompileStatic
class IntegrationTestTask extends DefaultTask {

  Appendable sout = new StringBuilder();

  File outputFile = new File("./temp_data/output.txt")
  File referenceFile = new File("./src/integrationTest/resources/reference.txt")

  String command = 'java -jar build/libs/VariantCaller-1.0-SNAPSHOT-all.jar' +
      ' --fasta ./src/integrationTest/resources/test1.fasta' +
      ' --bed ./src/integrationTest/resources/test1.bed' +
      ' --sam ./src/integrationTest/resources/test1.sam'

  /**
   * Tasks main method. Assembling JAR file and executing it, saving output to
   * temporary file and comparing it to the reference file.
   * @throws GradleException if test fails.
   */
  @TaskAction
  def run() {

    File temp_data = new File("./temp_data");
    if (temp_data.mkdir()) {
      System.out.println('temp_data not found, creating.');
    }

    Process runTest = command.execute()

    runTest.waitForProcessOutput(sout, System.err)

    if (sout.toString().isEmpty()) {
      throw new GradleException('No output from run task!')
    }

    outputFile.createNewFile()
    outputFile.write(sout.toString())

    if (!outputFile.text.equals(referenceFile.text)) {
      throw new GradleException('Output file and reference file do not match!')
    } else {
      println('Output file and reference file match!')
    }
  }
}
