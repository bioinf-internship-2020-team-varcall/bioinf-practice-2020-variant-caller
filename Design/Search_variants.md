# Поиск потенциальных вариантов

## Описание алгоритма

1. Последовательно читается каждый рид из входных данных.
2. Если хромосомы рида нет в словаре, то она добавляется как ключ.
3. Читается количество элементов в сигаре рида.
4. Происходит итерация по элементам сигары и получение позиций в риде и в референсе в соответствии с операторами элементов. Если в словаре, где ключ - это хромосома, полученной позиции из референса не существует, то она добавляется в словарь-значение для текущей хромосомы, а значением для позиции создается объект Variant. Затем в список внутри этого объекта записывается буква.
5. После того как все риды прочитаны и списки для каждой позиции составлены определяются потенциальные варианты - то есть для каждой хромосомы в словаре мы получаем все позиции и на этих позициях в геноме ищем букву и сравниваем с теми потенциальными вариантами, которые мы записали выше.
6. Вариантом считается та позиция, в списке которой есть хотя бы 2 буквы

## Оценка сложности алгоритма по времени

```O(Σ(количество элементов в сигаре k-ого рида * cуммарную длину элементов сигары k-ого рида), k ∈ 0..количество ридов)) + O(количество хромосом в собранном словаре * количество позиций для каждой хромосомы * длину списка потенциальных вариантов для каждой позиции)```

## Оценка сложности алгоритма по памяти

```O(количество ридов + количество последовательностей в fasta-файле + (количество ключей-хромосом * количество ключей-позиций * количество значений в списке потенциальных вариантов))```

## Изменения FastaHandler
```java
package com.epam.bioinf.variantcaller.handlers;

class FastaHandler {
  HashMap<Integer, ReferenceSequence> getFastaSequences(ParsedArguments parsedArguments); // Возвращаем прочитанные последовательности
}
```

## Новые классы

### Класс Caller
```java
package com.epam.bioinf.variantcaller.caller;

// Временная реализация, которая будет расширена в будущих версиях(задача про метрики)
class Caller {
  private HashMap<String, HashMap<Integer, Variant>> variants; // Храним варианты как словарь, где ключ - это контиг, а значение - другой словарь, где его ключ - это позиция в геноме, а значением - объект, хранящий потенциальные варианты
  List<Variant> call(HashMap<Integer, ReferenceSequence> referenceSequences, List<SAMRecord> samRecords); // Последовательности передаются как словарь, где ключ - это контиг индекс и значение - последовательность
  void initVariants(); // создаем и инициализируем варианты для нужных позиций из ридов
  void printPotentialVariants(); // Временный метод вывода вариантов в консоль
}
```

### Класс Variant
```java
package com.epam.bioinf.variantcaller.caller;

// Временная реализация, которая будет расширена в будущих версиях(задача про метрики)
class Variant {
  ArrayList<Character> potentialVariants; // Сохраняем все потенциальные варианты

  Variant(Character referenceChar, int position); // Инициализируем список
  void addPotentialVariant(Character potentialVariant); // Добавляем символ к potentialVariants
  public List<Character> getPotentialVariants(); // Возвращаем список потенциальных вариантов
}
```

## Unit-тесты

В unit-тестах используется "mock fasta example" - это пример fasta файла с небольшой последовательностью
и "mock sam example" - это примеры sam файлов с небольшим количеством ридов.

### Caller

* Caller must successully create variant objects according to cigars in reads
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

* Variant must successfully add character

## Интеграционный тест

* Caller must print correct variants with real fasta and sam
