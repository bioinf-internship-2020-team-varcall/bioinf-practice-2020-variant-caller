# Задание интервалов для поиска вариантов

## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

## Изменения в классах CommandLineParser и ParsedArguments

Так как реализация предусматривает получения данных интервала из командной строки, классы CommandLineParser и ParsedArguments будут доработаны.
Некоторые проверки и тесты будут изменены так как интервалы являются необязательным параметром.
Ниже приведенны изменения в классах, проверки не были описаны.

### Изменения в CommandLineParser
```java
package com.epam.bioinf.variantcaller.cmdline;

// Описаны только добавления к уже существующей реализации
public class CommandLineParser {
  
  private final String REGION_KEY = "region"; // Ключ для региона поиска
  
  OptionParser optionParser = new OptionParser() {
      {
        accepts(REGION_KEY); 
      }
  };
  
  OptionSpec<String> region = getOptionSpecByParameter(optionParser, REGION_KEY);
  parsedArguments = new ParsedArguments(
    options.valuesOf(region)
  );

  private OptionSpec<String> getOptionSpecStringByParameter(OptionParser parser, String key) {
    return parser
        .accepts(key)
        .withRequiredArg()
        .required()
        .ofType(String.class)
  }
}
```
### Изменения в ParsedArguments
```java
package com.epam.bioinf.variantcaller.cmdline;

// Описаны только добавления к уже существующей реализации
public class ParsedArguments {
  
  private final List<String> regionData;

  public List<String> getRegionData() {
    return Collections.unmodifiableList(regionData);
  }
}
```

## Класс IntervalsHandler

### Для проверки работоспособности:

* Вывод списка интервалов в stdout.

### Реализация

В реализации используются следующие классы из библиотеки [htsjdk](https://github.com/samtools/htsjdk):

* [BEDCodec](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/tribble/bed/BEDCodec.html)
* [FullBEDFeature](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/tribble/bed/FullBEDFeature.html)

Чтение интервалов и их запись в поле класса происходит в конструкторе класса.

```java
package com.epam.bioinf.variantcaller.handlers;

//Временная реализация для проверки работоспособности, она будет изменена в будущих версиях
class IntervalsHandler {
  IntervalsHandler(List<String> intervalData)); // Конструктор принимает данные для интервала параметром командной строки.
  // При вызове данного конструктора создается экземпляр FullBEDFeature и сохраняется в поле класса.
  IntervalsHandler(List<Path> pathsToFiles); // Конструктор принимает пути к файлам и сохраняет в поле класса список интервалов.
  
  private List<FullBEDFeature> intervals; // Хранение интервалов

  void listIntervals(); // Вывод списка интервалов в stdout  для проверки работоспособности

  List<FullBEDFeature> getIntervals() { // Получение списка интервалов
    return Collections.unmodifiableList(Arrays.asList(intervals));
  }
}
```

## Unit-тесты
* IntervalsHandler must fail if list of parsed intervals is empty
* IntervalsHandler must return correct if list of intervalData is valid
* IntervalsHandler must fail if list of intervalData is not valid
* IntervalsHandler must fail if list of intervalData is empty
* IntervalsHandler must fail if list of paths is empty
* IntervalsHandler must return correct if list of 1 path is valid
* IntervalsHandler must fail if list of 1 path is not valid
* IntervalsHandler must return correct if list of multiple paths is valid
* IntervalsHandler must fail if list of multiple paths is not valid
* IntervalsHandler must return correct if interval is valid
* IntervalsHandler must fail if interval is not valid
* IntervalsHandler must return correct if file can be decoded
* IntervalsHandler must fail if file can not be decoded
