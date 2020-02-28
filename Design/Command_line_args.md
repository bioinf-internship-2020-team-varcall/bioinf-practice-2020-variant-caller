# Поддержка аргументов командной строки

## Выбор билиотеки
Для парсинга агрументов командной строки используется библиотека jopt-simple.

## Примеры параметров и стиль наименования

[Примеры и стиль параметров jopt](http://jopt-simple.github.io/jopt-simple/examples.html)

### Примеры параметров

* Пример передачи одного пути к файлам

```
--fasta ref_gen.fasta
--bed transcription_regulation_spots.bed
--sam mapped_reads_sample2.sam
```

* Пример передачи нескольких путей к файлам

```
--fasta ref_gen.fasta
--bed e_coli_genes.bed:transcription_regulation_spots.bed
--sam mapped_reads_sample1.sam:mapped_reads_sample2.sam
```

### Описания параметров:

Корректным значением параметра '--fasta' является один путь к '.fasta' файлу.

```
--fasta значение
```

Корректным значением параметра '--bed' является один или несколько путей(разделенных сепаратором ';' или ':') к '.bed' файлам.

```
--bed значение
```

Корректным значением параметра '--sam' является один или несколько путей(разделенных сепаратором ';' или ':') к '.sam' файлам.

```
--sam значение
```

## Пакеты и классы программы

* CommandLineParser

```java
package com.epam.bioinf.variantcaller.cmdline;

class CommandLineParser {
  static CommandLineParser parse(String[] args); //Factory method, возвращает парсер и отвечает за валидацию аргументов, переданных ему в параметре, а также делегацию самого процесса парсинга объекту класса OptionParser из библиотеки jopt.
  OptionSet getOptions(); //возвращает объект OptionSet, который содержит в себе аргументы, разобранные в соответствии с параметрами parser, а также эти параметры
  Path getFastaPath(); //возвращает '.fasta' файл, полученный из переданного методу parse значения параметра --fasta, либо null если парсеру еще не удалось получить значение.
  List<Path> getBedPaths(); //возвращает список из одного или нескольких '.bed' файлов, полученных из переданного методу parse значения параметра --bed, либо null если парсеру еще не удалось получить значения 
  List<Path> getSamPaths() //возвращает список из одного или нескольких '.sam' файлов, полученных из переданного методу parse значения параметра --bed, либо null если парсеру еще не удалось получить значения;
}

```

* CommandLineMessages

```java
package com.epam.bioinf.variantcaller.cmdline;

class CommandLineMessages {} //отвечает за хранение константных сообщений об ошибках, используемых классом CommandLineParser

```

* ParsedData

```java
package com.epam.bioinf.variantcaller;

class ParsedData {
  static ParsedData createParsedDataFrom(String[] args); //Factory method, возвращает объект ParsedData на основе данных полученных от парсера, либо null, если произошла ошибка.
  File getResultFasta(); //возвращает '.fasta' файл, полученный от parser.
  File getResultBed(); //возвращает список из '.bed' файлов, полученных от parser.
  List<File> getResultSam(); //возвращает список из одного или нескольких '.sam' файлов, полученных от parser.
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

## Таблица тестов для класса ParsedData

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|parsedDataMustNotReturnNullWithValidParameters|Экземпляр ParsedData был успешно создан при верных параметрах|
|2|parsedDataMustReturnNullWithInvalidParameters|Класс ParsedData вернул null в при неверных параметрах|

## Таблица интеграционных тестов программы

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|programMustWorkWithCorrectArguments|Экземпляр ParsedData был успешно создан при верных параметрах, и результаты, полученные CommandLineParser соответствуют ожидаемым|
|2|programMustWorkWithInvalidArguments|Класс ParsedData вернул null в при неверных параметрах, а результаты не были получены|



