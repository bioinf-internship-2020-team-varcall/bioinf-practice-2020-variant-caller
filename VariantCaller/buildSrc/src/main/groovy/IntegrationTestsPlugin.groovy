import org.gradle.api.Plugin
import org.gradle.api.Project

class IntegrationTestsPlugin implements Plugin<Project>{

  void apply(Project project) {

    project.task('testProgramWithPresetArgs', type: TestProgramWithPresetArgs) {
      group = 'integration tests'
      description = 'Test program with preset arguments'
      dependsOn 'shadowJar'
    }

    project.task('integrationTests') {
      group = 'integration tests'
      description = 'Run all integration tests for the project'
      dependsOn 'testProgramWithPresetArgs'
    }
  }
}
