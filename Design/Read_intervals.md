# Задание интервалов для поиска вариантов

## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

## Класс IntervalsHandler

### Для проверки работоспособности:

* Вывод списка интервалов

### Реализация

В реализации используются следующие классы из библиотеки [htsjdk](https://github.com/samtools/htsjdk):

* [BEDCodec](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/tribble/bed/BEDCodec.html)
* [BEDFeature](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/tribble/bed/BEDFeature.html)

```java
package com.epam.bioinf.variantcaller.handlers;

//Временная реализация для проверки работоспособности, она будет изменена в будущих версиях
class IntervalsHandler {
  IntervalsHandler(int[] interval); // Конструктор принимает интервал параметром командной строки
  IntervalsHandler(List<Path> pathsToFiles); // Конструктор принимает пути к файлам и сохраняет в поле класса список интервалов.
  
  BEDFeature[] intervals; // Хранение интервалов если заданы файлы
  int[] interval; // // Хранение интервала

  void listIntervals(); // Вывод списка интервалов для проверки работоспособности
}
```

## Unit-тесты

* IntervalsHandler must return correct if interval is valid
* IntervalsHandler must fail if interval is not valid
* IntervalsHandler must return correct if file can be decoded
* IntervalsHandler must fail if file can not be decoded
