# Фильтрация выровненных ридов.
## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

## Изменения ParsedArguments
Для фильтрации вариантов необходима функция для проверки были ли заданы интервалы.
```java
public boolean isIntervalsSet();
```
Дополнения к unit-тестам:
  * ParsedArguments must return correct if intervals were specified
  * ParsedArguments must return false if intervals were not specified

## Изменения SamHandler

### Для проверки работоспособности:

* Вывод количества отфильтрованных ридов

### Реализация

Как будет выполнятся чтение, решается в конструкторе вызовом функции `isIntervalsSet()`.
Чтение ридов выполняется последовательно, в случае с фильтрацией, происходит проверка - находится
ли рид в заданных интервалах.
В методе проверки `isRelatedToIntervals`, если рид находится в заданном интервале он
добавляется в `samRecords`.

```java
package com.epam.bioinf.variantcaller.handlers;

//Временная реализация, может быть изменена изменена в будущих версиях
class SamHandler {
  private ArrayList<SAMRecord> samRecords; //Хранятся прочитанные и отфильтрованные риды.
  private List<BEDFeature>
  
  public SamHandler(ParsedArguments parsedArguments) { //Конструктор принимает провалидированные parsed arguments.
    if (parsedArguments.isIntervalsSet()) {
      IntervalsHandler intervalsHandler = new IntervalsHanler(parsedArguments);
      readInSpecifiedIntervals(parsedArguments.getSamPaths(), intervalsHandler.getIntervals());
    } else {
      read(parsedArguments.getSamPaths());
    }
  }

  private static void readInSpecifiedIntervals(List<Path> samPaths, List<BEDFeature> intervals);

  private static void read(List<Path> samPaths);

  private static void isRelatedToIntervals(SAMRecord record);
}
```

## Unit-тесты
Описанны только добавления к существующим

* SamHandler must return correct reads number with multiple files by single interval
* SamHandler must return correct reads number with one file filtered by single interval
* SamHandler must return correct reads number with multiple files by multiple interval
* SamHandler must return correct reads number with one file filtered by multiple interval
* SamHandler must fail if no reads relate to provided intervals
