# Чтение fasta референсного генома

## Используется библиотека htsjdk

## Класс FastaHandler

```java
package com.epam.bioinf.variantcaller.handlers;

class FastaHandler {
  FastaHadler(Path pathToFastaFile); //конструктор принимает путь к fasta файлу, создает на основе него FastaSequenceFile, из которого получает и сохраняет ReferenceSequence. Также проверяется, что файл содержит только 1 последовательность.
  double gcContent() //получает gcContent из сохраненной последовательности;
  int countNucleotides() //считает нуклеотиды в сохраненной последовательности;
}
```

## Unit-тесты

```java
class FastaHandlerTest {
  @Test
  void fastaHandlerMustReturnCorrectGcContentWithMockExample();
  @Test
  void fastaHandlerMustReturnCorrectCountOfNucleotidesWithMockExample();
  @Test
  void fastaHandlerMustReturnCorrectCountOfNucleotidesWithRealExample();
  @Test
  void fastaHandlerMustReturnCorrectNucleotidesCountWithRealExample();
  @Test
  void fastaHandlerMustFailIfNoSequenceWasProvided();
  @Test
  void fastaHandlerMustFailIfMultipleSequencesWereProvided();
}
```
