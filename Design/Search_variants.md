# Поиск потенциальных вариантов

## Описание алгоритма

1. Для каждой позиции в геноме создается объект Variant со списком всех соответствующих букв в выровненных ридах, первоначально пустой. Также Variant хранит саму позицию и букву.
2. Последовательно читается каждый рид из входных данных
3. Для каждой буквы в риде определяется ее координата с учетом выравнивания
4. Буква добавляется к списку для ее позиции
5. После того как все риды прочитаны и списки для каждой позиции составлены определяются потенциальные варианты
6. Вариантом считается та позиция, в списке которой есть хотя бы 2 буквы

## Оценка сложности алгоритма по времени

```O((a * b) + (Σdk, k ∈ 0..c) + (Σmt, t ∈ 0..a * b))```, где:
* ```a``` - количество хромосом
* ```b``` - длина последовательности в хромосоме
* ```c``` - количество ридов
* ```dk``` - количество символов в ```k```-ом риде
* ```m``` - количество символов в ```t```-ом списке с потенциальными вариантами

Мы дважды проходим по последовательности(в первый раз по пункту 1 инициализируем список - ```a * b```, а во второй по пунктам 5 и 6 идем по этому списку и для каждого элемента спрашиваем количество вариантов - это занимает ```Σmt, t ∈ 0..a * b```). А для пунктов 2, 3 и 4 имеем ```Σdk, k ∈ 0..c``` операций.

## Оценка сложности алгоритма по памяти

```O(a * b)```, где:
* ```a``` - количество хромосом
* ```b``` - длина последовательности в хромосоме

Мы создаем ```a * b``` объектов для каждой позиции в геноме. Размер каждого такого объекта зависит от количества приходящихся на эту позицию символов в ридах.

## Изменения FastaHandler
```java
package com.epam.bioinf.variantcaller.handlers;

class FastaHandler {
  List<FastaSequence> getFastaSequences(); // Возвращаем прочитанные последовательности
}
```

## Новые классы

### Класс Caller
```java
package com.epam.bioinf.variantcaller.caller;

// Временная реализация, которая будет расширена в будущих версиях(задача про метрики)
class Caller {
  List<Variant> variants; // Храним варианты
  Caller(List<FastaSequence> reference); // Инициализируем список вариантов объектами Variant с пустыми списками внутри
  List<Variant> call(List<SamRecord> reads); // Ищем варианты(заполняем массивы в объектах Variant) и возвращаем результат как список вариантов
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
  void addPotentialVariant(Character potentialVariant); // Добавляем символ к potentialVariants
  Character identifyMostPossibleVariant(); // Возвращаем наиболее часто встречающийся символ
}
```

## Unit-тесты

В unit-тестах используется "mock fasta example" - это пример fasta файла с небольшой последовательностью
и "mock sam example" - это примеры sam файлов с небольшим количеством ридов.

### Caller

* Caller must return correct variants with mock fasta, sam examples for one chromosome
* Caller must return correct variants with mock fasta, sam examples and one interval for one chromosome
* Caller must return correct variants with mock fasta, sam examples and multiple intervals for one chromosome
* Caller must return correct variants with mock fasta, sam examples for multiple chromosomes
* Caller must return correct variants with mock fasta, sam examples and one interval for multiple chromosomes
* Caller must return correct variants with mock fasta, sam examples and multiple intervals for multiple chromosomes
* Caller must return an empty array if no variants were found
* Caller must throw an exception if no reference sequence was provided
* Caller must throw an exception if a list of reads is empty
* Caller must return filtered variants so that positions with less than two symbols were not considered as correct


### Variant

* Variant must successfully identify most possible variant

## Интеграционный тест

* Caller must print correct variants with real fasta and sam
