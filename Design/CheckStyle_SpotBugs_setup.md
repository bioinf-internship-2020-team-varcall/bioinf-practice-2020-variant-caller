# Инструкция по установке и запуску Checkstyle и SpotBugs #

Все действия необходимо проводить в корневой папке проекта VariantCaller.

## Установка Checkstyle ##
Для того, чтобы использовать плагин Checkstyle, необходимо добавить его в ``` build.gradle ``` следующим образом:
```
plugins {
    id 'checkstyle'
}
```
## Запуск Checkstyle ##

Для запуска Checkstyle используются следующие задачи: ```checkstyleMain``` и ```checkstyleTest.
