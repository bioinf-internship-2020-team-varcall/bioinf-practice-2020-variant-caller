# Поиск потенциальных вариантов

## Изменения FastaHandler
```java
package com.epam.bioinf.variantcaller.handlers;

class FastaHandler {
  FastaSequence getFastaSequence(); // Возвращаем прочитанную последовательность
}
```

## Новые классы

### Класс Caller
```java
package com.epam.bioinf.variantcaller.caller;

// Временная реализация, которая будет расширена в будущих версиях(задача про метрики)
class Caller {
  Variant[] variants; // Храним варианты
  List<SamRecords> reads; // Храним риды

  Caller(FastaSequence reference, List<SamRecord> reads); // Инициализируем массив вариантов объектами Variant с пустыми массивами внутри
  Variant[] call(); // Ищем варианты(заполняем массивы в объектах Variant) и возвращаем результат
  void printPotentialVariants(); // Временный метод вывода вариантов в консоль
}
```

### Класс Variant
```java
package com.epam.bioinf.variantcaller.caller;

// Временная реализация, которая будет расширена в будущих версиях(задача про метрики)
class Variant {
  Character referenceChar; // Храним референс символ
  int position; // Храним индекс референс символа в геноме
  ArrayList<Character> potentialVariants; // Сохраняем все потенциальные варианты

  Variant(Character referenceChar, int position); // Инициализируем список
  void addPotentialVariant(); // Добавляем символ к potentialVariants
  Character identifyMostPossibleVariant(); // Возвращаем наиболее часто встречающийся символ
}
```

## Unit-тесты

В unit-тестах используется "mock fasta example" - это пример fasta файла с небольшой последовательностью
и "mock sam example" - это примеры sam файлов с небольшим количеством ридов.

### Caller

* Caller must return correct variants with mock fasta, sam examples
* Caller must return correct variants with mock fasta, sam examples and one interval
* Caller must return correct variants with mock fasta, sam examples and multiple intervals
* Caller must return an empty array if no variants were found
* Caller must throw an exception if no reference sequence was provided
* Caller must throw an exception if a list of reads is empty
* Caller must return filtered variants so that positions with less than two symbols were not considered as correct


### Variant

* Variant must successfully identify most possible variant

## Интеграционный тест

* Caller must print correct variants with real fasta and sam
