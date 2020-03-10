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
  static CommandLineParser build(String[] args); //Factory method, возвращает парсер и отвечает за делегацию самого процесса парсинга объекту класса OptionParser из библиотеки jopt, а валидацию и хранение полученных данных ParsedData.
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

* ParsedArguments

```java
package com.epam.bioinf.variantcaller;

class ParsedArguments {
  ParsedArguments(List<Path> fastaPaths, List<Path> bedPaths, List<Path> samPaths); //Создает объект на основе данных, полученных от парсера, а также валидирует эти данные
  Path getFastaPath(); //возвращает '.fasta' файл, полученный от parser.
  List<Path> getBedPaths(); //возвращает список из '.bed' файлов, полученных от parser.
  List<Path> getSamPath(); //возвращает список из одного или нескольких '.sam' файлов, полученных от parser.
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
|2|parserMustBeBuiltWithValidParameters|CommandLineParser успешно создан при верных параметрах|
|3|parserMustFailWithInvalidParameters|CommandLineParser бросил Exception при неверных параметрах|
|4|parserMustReturnCorrectParsedArgumentsWithValidArguments|При верных параметрах CommandLineParser возвращает корректный ParsedArguments|
|5|parserMustReturnCorrectParsedArgumentsIfMultipleArgumentsProvided|При верных нескольких параметрах CommandLineParser возвращает корректный ParsedArguments|

## Таблица тестов для класса ParsedArguments

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|parsedArgumentsMustBeCreatedWithValidParameters|Экземпляр класса ParsedArguments был успешно создан при верных параметрах|
|2|parsedArgumentsMustFailIfLessThanOneBedPathProvided|ParsedData бросил Exception при отсутствии значений параметра --bed|
|3|parsedArgumentsMustFailIfLessThanOneSamPathProvided|ParsedData бросил Exception при отсутствии значений параметра --sam|
|4|parsedArgumentsMustFailIfLessThanOneFastaPathProvided|ParsedData бросил Exception при отсутствии значений параметра --fasta|
|5|parsedArgumentsFailIfMoreThanOneFastaPathProvided|ParsedData бросил Exception при нескольких значениях параметра --fasta|
|6|parsedArgumentsMustBeBuiltWithRemovedDuplicatedPaths|Экземпляр ParsedArguments был успешно создан, а копии были удалены|
|7|parsedArgumentsMustFailIfFastaPathHasInvalidExtension|ParsedData бросил Exception, если значение параметра --fasta имеет не '.fasta' расширение|
|8|parsedArgumentsMustFailIfSomeBedPathHasInvalidExtension|ParsedData бросил Exception, если хоть одно значение параметра --bed имеет не '.bed' расширение|
|9|parsedArgumentsMustFailIfSomeSamPathHasInvalidExtension|ParsedData бросил Exception, если хоть одно значение параметра --sam имеет не '.sam' расширение|
|10|parsedArgumentsMustFailIfFastaFileDoesNotExist|ParsedData бросил Exception, если путь, указанный в параметре --fasta, не представляет собой путь к существующему '.fasta' файлу|
|11|parsedArgumentsMustFailIfSomeBedFileDoesNotExist|ParsedData бросил Exception, если хоть один путь, указанный в параметре --bed, не представляет собой путь к существующему '.bed' файлу|
|12|parsedArgumentsMustFailIfSomeSamFileDoesNotExist|ParsedData бросил Exception, если хоть один путь, указанный в параметре --sam, не представляет собой путь к существующему '.sam' файлу|

## Таблица интеграционных тестов программы

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|programMustWorkWithCorrectArguments|Программа отработала без исключений при верных параметрах|
|2|programMustFailWithInvalidArguments|При неверных аргументах, программа бросила исключение с сообщением о неверном числе значений параметра --fasta|



