# Чтение [SAM](https://en.wikipedia.org/wiki/SAM_(file_format) выровненных ридов.
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
  SamHadler(List<Path> pathsToSamFiles); //Конструктор принимает пути к файлам и сохраняет в поле класса.
 Map<Path, Long> computeReadsByPaths() //Вычисляет количество выровненных ридов для каждого полученного файла.
}
```

## Unit-тесты

В unit-тестах используется "mock example" - это примеры sam файлов с небольшим количеством ридов.

* SamHandler must return correct reads number with mock examples
* SamHandler must fail if invalid file was provided

## Интеграционные тесты

В интеграционных тестах используется "real example" - это пример sam файла.

*  SamHandler must return correct reads number with real example
