### Интеграционные тесты

------------

##### Использование:
- Запуск программы с исходными параметрами и входными данными.  
- Проверка результатов.

------------
##### Gradle API
 - Задача реализована как отдельный Gradle task на основе возможностей org.gradle.api,
 написанный на groovy.

-----------
##### Gradle Shadow jar plugin
 - Используется для сборки выполняемого jar файла со всеми зависимостями. [Github](https://github.com/johnrengelman/shadow)

------------

##### Структура
 - Добавлена директория *buildSrc*  для отдельной сборки любых задач, плагинов или других классов, которые предназначены для использования в сценариях сборки основной сборки, но не должны использоваться совместно. [Gradle Docs](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources)

```
buildSrc
    src
        main
            groovy
src
build.gradle
```
------------

##### Классы и задачи
 - IntegrationTestTask
 ```groovy
class IntegrationTestTask extends DefaultsTask {
	@TaskAction
	def start() // запускает выполнение процесса.
        Process.execute('java -jar variantCaller.jar') // запуск jar файла с аргументами
}
```
 - Результат запустка записывается и сверяется.

 - task int_test  - задача в gradle.build - запускает IntegrationTestTask

------------

##### Временные данные
 - Создается директория `temp-data`, если она отсутствует. В неё записываются результаты теста.
   После проверки результаты удаляются. Директория удаляется командой `gradle clean`.

------------

##### Пример запуска
 - Windows:  `gradlew.bat int_test`

 - Unix:  `./gradlew int_test`
