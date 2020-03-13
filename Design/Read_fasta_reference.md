# Чтение [FASTA](https://en.wikipedia.org/wiki/FASTA_format) референсного генома

## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

## Класс FastaHandler

### Для проверки работоспособности:

* Вычисляется [GC content](https://en.wikipedia.org/wiki/GC-content)
* Вычисляется  количество нуклеотидов в последовательности.

### Реализация

В реализации используются следующие классы из библиотеки [htsjdk](https://github.com/samtools/htsjdk):

* [ReferenceSequence](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/samtools/reference/ReferenceSequence.html)
* [FastaSequenceFile](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/samtools/reference/FastaSequenceFile.html)


```java
package com.epam.bioinf.variantcaller.handlers;

//Временная реализация для проверки работоспособности, она будет изменена в будущих версиях
class FastaHandler {
  FastaHadler(Path pathToFastaFile); //Конструктор принимает путь к файлу и сохраняет в поле класса разобранный результат (ReferenceSequence). Также проверяется, что файл содержит только 1 последовательность.
  double getGcContent() //получает GC content из сохраненной последовательности;
  int countNucleotides() //считает нуклеотиды в сохраненной последовательности;
}
```

## Unit-тесты

В unit-тестах используется "mock example" - это пример fasta файла с искуственно созданной короткой последовательностью

* FastaHandler must return correct GC content with mock example
* FastaHandler must return correct count of nucleotides with mock example
* FastaHandler must fail if no sequence was provided
* FastaHandler must fail if multiple sequences were provided

## Интеграционные тесты

В интеграционных тестах используется "real example" - это пример fasta файла с одной из реальных [сборок генома кишечной палочки](https://www.ncbi.nlm.nih.gov/genome/167?genome_assembly_id=161521)

* FastaHandler must return correct GC content with real example
* FastaHandler must return correct count of nucleotides with real example
