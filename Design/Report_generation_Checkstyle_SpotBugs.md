# Генерация отчетов Checkstyle и SpotBugs #

## Генерация отчетов Checkstyle ##

После запуска задач checkstyleMain и checkstyleTest, создаются следующие файлы с отчетами:
``` main.html ```, ``` main.xml ```, ``` test.html ```, ``` test.xml ```.
Файлы находятся в папке:
```
src
  -build
    -reports
      -checkstyle
```

## Генерация отчетов SpotBugs ##

После запуска задач spotbugsMain и spotbugsTest, создаются следующие файлы с отчетами:
``` main.xml ```, ``` test.xml ```.
Файлы находятся в папке:
```
src
  -build
    -reports
      -spotbugs
```