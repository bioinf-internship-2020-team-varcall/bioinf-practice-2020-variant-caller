# Поиск потенциальных вариантов

## Описание алгоритма

1. Для каждой затронутой хромосомы в поиске, создается Hashmap, где ключ - название хромосомы,
  значение - пустой Hashmap, у которого ключ будет - позиция нуклеотида отсносительно хромосомы, а значение -
  класс PotentialVariant.
2. Последовательно читается каждый рид из входных данных.
3. Для рида из референс файла получаем последовательность которую покрывает этот рид.
3. Для каждой буквы в риде определяется ее координата с учетом выравнивания.
4. Идя по риду, проверям есть ли в Hashmap для текущей позиции значение:
  * если есть, то проверям отличается ли от нуклеотида на референсе и добавляем нуклеотид в список возможных вариантов.
  * если нет, то создаем для этой позиции PotentialVariant и проверяем отличается ли от нуклеотида референсе и добавляем букву в список возможных вариантов.
5. После того как все риды прочитаны и списки для каждой позиции составлены определяются варианты.
6. Вариантом считается та позиция, в списке которой есть хотя бы 2 буквы.

## Оценка сложности алгоритма по времени

O(количество ридов) * O(размер рида) + O(количество измененных позиций)

## Оценка сложности алгоритма по памяти

O(количество хромосом покрытых ридами) * O(количество измененных позиций)

Мы создаем объект для каждой измененной позиции в геноме. Размер каждого такого объекта зависит от количества приходящихся на эту позицию символов в ридах, отличающихся от референса.

## Изменения FastaHandler(уже добавленно в проект)
```java
package com.epam.bioinf.variantcaller.handlers;

class FastaHandler {
  IndexedFastaSequenceFile fastaSequenceFile;
  ReferenceSequence getSubsequence(String contig, long start, long stop);
}
```

## Новые классы

### Класс Caller
Класс отвечающий за нахождение вариантов.
```java
package com.epam.bioinf.variantcaller.caller;

// Временная реализация, которая будет расширена в будущих версиях(задача про метрики)
class Caller {
  private HashMap<String, HashMap<Integer, Variant>> variants; // Храним варианты как словарь, где ключ - это контиг, а значение - другой словарь, где его ключ - это позиция в геноме, а значением - объект, хранящий потенциальные варианты
  List<Variant> call(HashMap<Integer, ReferenceSequence> referenceSequences, List<SAMRecord> samRecords); // Последовательности передаются как словарь, где ключ - это контиг индекс и значение - последовательность
  void printPotentialVariants(); // Временный метод вывода вариантов в консоль
}
```

### Класс PotentialVariant
Класс используемый в `Caller` при нахождении вариантов
```java
package com.epam.bioinf.variantcaller.caller;

// Временная реализация, которая будет расширена в будущих версиях(задача про метрики)
class PotentialVariant {
  ArrayList<Character> potentialVariants; // Сохраняем все потенциальные варианты

  PotentialVariant(Character referenceChar); // Инициализируем список
  void addPotentialVariant(Character potentialVariant); // Добавляем символ к potentialVariants
  public List<Character> getBases(); // Возвращаем список потенциальных вариантов
  ArrayList<Allele> getVariants(); // Получение вариантов для вывода VCF
}
```

### Класс Variant
Класс является результатом работы `Caller`, содержит всю информацию для вывода VCF.
```java
package com.epam.bioinf.variantcaller.caller;
class Variant {
  String contig;
  int pos;
  ArrayList<Allele> variants;
  Allele refAllele;
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
