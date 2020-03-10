# Инструкция по установке и запуску Checkstyle и SpotBugs #

Все действия необходимо проводить в корневой папке проекта VariantCaller.

## Установка Checkstyle ##

Для того, чтобы использовать плагин Checkstyle, необходимо добавить его в ``` build.gradle ``` следующим образом:
```
plugins {
    id 'checkstyle'
}

def configDir = "${project.rootDir}/config"
checkstyle {
    toolVersion '7.8.1'
    configFile file("$configDir/checkstyle/checkstyle.xml")
}

checkstyleMain {
    source ='src/main/java'
}

checkstyleTest {
    source ='src/test/java'
}
```
## Запуск Checkstyle ##

Для запуска Checkstyle используются следующие задачи: ```checkstyleMain``` и ```checkstyleTest```.

## Установка SpotBugs ##

Для того, чтобы использовать плагин SpotBugs, необходимо добавить его в ``` build.gradle ``` следующим образом:
```
plugins {
    id "com.github.spotbugs" version "3.0.0"
}

sourceSets {
    main {
        java.srcDirs = ['src/main/java']
    }
}

dependencies {
    spotbugsPlugins 'com.h3xstream.findsecbugs:findsecbugs-plugin:1.7.1'
}

spotbugs {
    toolVersion = '4.0.0'
    ignoreFailures = false
    effort = 'max'
    showProgress = true
}

tasks.withType(com.github.spotbugs.SpotBugsTask) {
    reports {
        xml.enabled = true
        html.enabled = false
    }
}
```
## Запуск SpotBugs ##

Для запуска SpotBugs используются следующие задачи: ```spotbugsMain``` и ```spotbugsTest```.