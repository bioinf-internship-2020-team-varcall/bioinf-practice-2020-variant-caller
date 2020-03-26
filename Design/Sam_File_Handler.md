# Чтение [SAM](https://en.wikipedia.org/wiki/SAM_(file_format)) выровненных ридов.
## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

## Класс SamHandler

### Для проверки работоспособности:

* Вычисляется количество выровненных ридов

### Реализация

В реализации используются следующие классы из библиотеки [htsjdk](https://github.com/samtools/htsjdk):
* [SamReaderFactory](https://www.javadoc.io/doc/com.github.samtools/htsjdk/2.5.1/htsjdk/samtools/SamReaderFactory.html)


```java
package com.epam.bioinf.variantcaller.handlers;

//Временная реализация для проверки работоспособности, она будет изменена в будущих версиях
class SamHandler {
  SamReaderFactory factory;
  
  SamHadler(List<Path> pathsToSamFiles); //Конструктор принимает пути к файлам и сохраняет в поле класса.
  
  /**
    Метод является временным, поэтому данные после прочтения не хранятся в поле класса.
    Каждый новый вызов метода ведет к новому прочтению данных.
  */
  Map<Path, Long> countReadsByPath() {
    pathsToSamFiles.foreach {
      factory.open(path) //Производится чтение файла внутри метода.
      ...
    }
  }
}
```

## Unit-тесты

В unit-тестах используется "mock example" - это примеры sam файлов с небольшим количеством ридов.

* SamHandler must return correct reads number with one file
* SamHandler must return correct reads number with multiple files
* SamHandler must fail if invalid or empty file provided
* SamHandler must fail if empty paths list provided
* SamHandler must fail if file contains only one read
* SamHandler must fail if provided file does not exist
* SamHandler must fail if one provided file is not SAM format
* SamHandler must fail if one of provided files is not SAM format
* SamHandler must fail if two or more paths to SAM files mathes
* SamHandler must fail if read occurs several times

## Интеграционные тесты

В интеграционных тестах используется "real example" - это [пример](http://genome.ucsc.edu/goldenPath/help/bam.html) [sam](http://genome.ucsc.edu/goldenPath/help/examples/samExample.sam) файла.

*  SamHandler must return correct reads number with real example
