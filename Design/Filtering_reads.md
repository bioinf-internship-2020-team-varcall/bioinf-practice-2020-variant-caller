# Фильтрация выровненных ридов.
## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

## Изменения ParsedArguments
Для фильтрации вариантов необходима функция для проверки были ли заданы интервалы.
```java
public boolean isIntervalsSet();
```
Дополнения к unit-тестам:
  * ParsedArguments must return true if intervals were specified
  * ParsedArguments must return false if intervals were not specified

## Изменения SamHandler

### Для проверки работоспособности:

* Вывод количества отфильтрованных ридов

### Реализация

Как будет выполняться чтение, решается в конструкторе вызовом функции `isIntervalsSet()`.
Чтение ридов выполняется последовательно. В случае с фильтрацией происходит проверка - находится
ли рид в заданных интервалах.
В методе `isRelatedToIntervals`, в случае если рид находится в заданном интервале он
добавляется в `samRecords`.

```java
package com.epam.bioinf.variantcaller.handlers;

// Временная реализация, может быть изменена изменена в будущих версиях
class SamHandler {
  private List<SAMRecord> samRecords; // Хранятся прочитанные и отфильтрованные риды.
  private List<BEDFeature>;
  
  public SamHandler(ParsedArguments parsedArguments) { // Конструктор принимает провалидированные parsed arguments.
    if (parsedArguments.isIntervalsSet()) {
      IntervalsHandler intervalsHandler = new IntervalsHanler(parsedArguments);
      readInSpecifiedIntervals(parsedArguments.getSamPaths(), intervalsHandler.getIntervals());
    } else {
      read(parsedArguments.getSamPaths());
    }
  }

  private static List<SAMRecord> read(List<Path> samPaths);

  private static List<SAMRecord> read(List<Path> samPaths, List<BEDFeature> intervals);

  private static boolean isInsideAnyInterval(SAMRecord record);
}
```

## Unit-тесты
Описанны только добавления к существующим

* SamHandler must return correct reads number with one file filtered by single interval
* SamHandler must return correct reads number with one file filtered by multiple intervals
* SamHandler must return correct reads number with multiple files filtered by single interval
* SamHandler must return correct reads number with multiple files filtered by multiple intervals
* SamHandler must fail if no reads relate to provided intervals
