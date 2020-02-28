# Поддержка аргументов командной строки

## Выбор билиотеки
Для парсинга агрументов командной строки используется библиотека jopt-simple.

## Примеры параметров и стиль наименования

[Примеры и стиль параметров jopt](http://jopt-simple.github.io/jopt-simple/examples.html)

### Примеры параметров

* Пример передачи одного пути к файлам

```
---fasta ref_gen.fasta
---bed transcription_regulation_spots.bed
---sam mapped_reads_sample2.sam
```

* Пример передачи нескольких путей к файлам

```
---fasta ref_gen.fasta
---bed e_coli_genes.bed:transcription_regulation_spots.bed
---sam mapped_reads_sample1.sam:mapped_reads_sample2.sam
```

### Описания параметров:

Корректным значением параметра '--fasta' является один путь к '.fasta' файлу.

```
---fasta значение
```

Корректным значением параметра '--bed' является один или несколько путей(разделенных сепаратором ';' или ':') к '.bed' файлам.

```
---bed значение
```

Корректным значением параметра '--sam' является один или несколько путей(разделенных сепаратором ';' или ':') к '.sam' файлам.

```
---sam значение
```

## Пакеты и классы программы

* CommandLineParser

```java
package com.epam.bioinf.variantcaller.cmdline;

class CommandLineParser {
  static void parse(String[] args); //отвечает за валидацию аргументов, переданных ему в параметре, а также делегирует сам процесс парсинга объекту parser класса OptionParser.
  static OptionSet getOptions(); //возвращает объект OptionSet, который содержит в себе аргументы, разобранные в соответствии с параметрами parser, а также эти параметры
  static File getFastaPath(); //возвращает список из одного '.fasta' файла, полученного из переданного методу parse значения параметра --fasta, либо null если парсеру еще не удалось получить значение.
  static List<File> getBedPaths(); //возвращает список из одного или нескольких '.bed' файлов, полученных из переданного методу parse значения параметра --bed, либо null если парсеру еще не удалось получить значения 
  public List<File> getSamPaths() //возвращает список из одного или нескольких '.sam' файлов, полученных из переданного методу parse значения параметра --bed, либо null если парсеру еще не удалось получить значения;
}

```

* CommandLineMessages

```java
package com.epam.bioinf.variantcaller.cmdline;

class CommandLineMessages {} //отвечает за хранение константных сообщений об ошибках, используемых классом CommandLineParser

```

* Launcher

```java
package com.epam.bioinf.variantcaller;

class Launcher {
  static int start(String[] args); //вызывает метод CommandLineParser parse, сохраняет результат и возвращает 0, если парсинг прошел успешно, или 1 в случае ошибки.
  static File getResultFasta(); //возвращает '.fasta' файл, полученный от parser, либо null если Launcher еще не получил значение от parser.
  static File getResultBed(); //возвращает список из '.bed' файлов, полученных от parser, либо null если Launcher еще не получил значение от parser.
  static List<File> getResultSam(); //возвращает список из одного или нескольких '.sam' файлов, полученных от parser, либо null если Launcher еще не получил значение от parser.
}

```

* Main

```java
package com.epam.bioinf.variantcaller;

public class Main {
  public static void main(String[] args) {} //метод получает аргументы командной строки и запускает программу
}

```


## Таблица тестов для класса CommandLineParser

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|parserMustAcceptValidParameters|Настройки CommandLineParser по допустимым аргументам были выставлены правильно|
|2|parserMustFailIfInvalidParameters|CommandLineParser бросает OptionException, при передаче недопустимых параметров|
|3|parserMustAcceptMultipleArguments|CommandLineParser может принимать несколько значений параметров ---bed и ---sam|
|4|parserMustFailIfMoreThanOneFastaPathProvided|CommandLineParser бросает OptionException при передаче нескольких значений параметра --fasta|
|5|parserMustFailIfLessThanOneFastaPathProvided|CommandLineParser бросает OptionException при отсутствии значений параметра --fasta|
|6|parserMustFailIfLessThanOneBedPathProvided|CommandLineParser бросает OptionException при отсутствии значений параметра --bed|
|7|parserMustFailIfLessThanOneSamPathProvided|CommandLineParser бросает OptionException при отсутствии значений параметра --sam|
|8|parserMustRemoveDuplicatedPaths|CommandLineParser корректно удаляет дупликаты путей к файлам|
|9|parserMustFailIfFastaPathHasInvalidExtension|CommandLineParser бросает OptionException, если значение параметра --fasta имеет не '.fasta' расширение|
|10|parserMustFailIfSomeBedPathHasInvalidExtension|CommandLineParser бросает OptionException, если хоть одно значение параметра --bed имеет не '.bed' расширение|
|11|parserMustFailIfSomeSamPathHasInvalidExtension|CommandLineParser бросает OptionException, если хоть одно значение параметра --sam имеет не '.sam' расширение|
|12|parserMustFailIfFastaFileDoesNotExist|CommandLineParser бросает OptionException, если путь, указанный в параметре --fasta, не представляет собой путь к существующему '.fasta' файлу|
|13|parserMustFailIfSomeBedFileDoesNotExist|CommandLineParser бросает OptionException, если хоть один из путей, указанных в параметре --bed, не представляет собой путь к существующему '.bed' файлу|
|14|parserMustFailIfSomeSamFileDoesNotExist|CommandLineParser бросает OptionException, если хоть один из путей, указанных в параметре --sam, не представляет собой путь к существующему '.sam' файлу|

## Таблица тестов для класса Launcher

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|launcherMustReturnZeroWithValidParameters|Экземпляр Launcher вернул 0 в при верных параметрах|
|2|launcherMustReturnOneWithInvalidParameters|Экземпляр Launcher вернул 1 в при неверных параметрах|
|3|launcherMustReturnNullIfResultFilesRequestedWithInvalidParameters|Экземпляр Launcher вернул null для всех результатов при неверных параметрах|

## Таблица интеграционных тестов программы

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|programMustWorkWithCorrectArguments|Экземпляр Launcher вернул 0 в при верных параметрах и результаты, полученные CommandLineParser соответствуют ожидаемым|
|2|programMustWorkWithInvalidArguments|Экземпляр Launcher вернул 1 в при неверных параметрах, а результаты не были получены|



