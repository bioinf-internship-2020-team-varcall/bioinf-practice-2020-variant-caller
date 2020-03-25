# Задание интервалов для поиска вариантов

## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

## Класс IntervalsHandler

### Реализация

В реализации используются следующие классы из библиотеки [htsjdk](https://github.com/samtools/htsjdk):

* [BEDCodec](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/tribble/bed/BEDCodec.html)
* [BEDFeature](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/tribble/bed/BEDFeature.html)

```java
package com.epam.bioinf.variantcaller.handlers;

class IntervalsHandler {
  IntervalsHandler(Path pathToFile); // Конструктор принимает путь к файлу и сохраняет в поле класса список интервалов.
  BEDFeature[] intervals; // Хранение интервалов
}
```

## Unit-тесты

* IntervalsHandler must return correct if file can be decoded
* IntervalsHandler must fail if file can not be decoded
