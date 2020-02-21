# Первоначальная настройка проекта Gradle

## Структура каталогов проекта VariantCaller
```
src
    -main
        -java
	    -com
		-example	
        -resources
    -test
        -java
        -resources
```
main/java — это java-файлы проекта, main/resources — это остальные файлы (*.properties, *.xml, *.img и прочие). В test находятся файлы необходимые для тестирования.

## Стадии сборки
В проекте уже подключены такие gradle плагины, как java и application, необходимые для выполнения задач сборки. Также подключены плагины idea и eclipse, которые используются для создания проектов в IntelliJIDEA и Eclipse соответственно.

Затем, если была выполнена команда ```./gradlew build```, происходит загрузка недостающих зависимостей из центрального репозитория Maven. После этого, происходит компиляция с использованием подключенных библиотек и запуск тестов. Лог можно увидеть ниже:

```
> Task :compileJava UP-TO-DATE
> Task :processResources NO-SOURCE
> Task :classes UP-TO-DATE
> Task :jar UP-TO-DATE
> Task :startScripts UP-TO-DATE
> Task :distTar UP-TO-DATE
> Task :distZip UP-TO-DATE
> Task :assemble UP-TO-DATE
> Task :compileTestJava UP-TO-DATE
> Task :processTestResources NO-SOURCE
> Task :testClasses UP-TO-DATE
> Task :test UP-TO-DATE
> Task :check UP-TO-DATE
> Task :build UP-TO-DATE
```

## Инструкция по сборке проекта
1. Зайти в корневую папку проекта VariantColler.
2. Ввести команду ```./gradlew build```, если вы хотите только собрать проект.
3. Ввести команду ```./gradlew run``` для сборки проекта и запуска программы.
4. Если вы используете ide для запуска программы, то вместо шага 3 в IntelliJIDEA нужно ввести команду ```./gradlew openIdea```, а в ```Eclipse ./gradlew eclipse```. Это сгенерирует необходимые файлы для ide, где после выполнения команды, вы сможете открыть проект и запустить программу.
