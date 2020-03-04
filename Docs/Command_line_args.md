# Инструкция по передаче параметров программе

### Примеры параметров

* Пример передачи одного пути к файлам

```
./gradlew run --args="--fasta ref_gen.fasta 
--bed transcription_regulation_spots.bed 
--sam mapped_reads_sample2.sam"
```

* Пример передачи нескольких путей к файлам

```
./gradlew run --args="--fasta ref_gen.fasta
--bed e_coli_genes.bed:transcription_regulation_spots.bed
--sam mapped_reads_sample1.sam:mapped_reads_sample2.sam"
```

### Описания параметров:

Корректным значением параметра '--fasta' является один путь к '.fasta' файлу.

```
--fasta значение
```

Корректным значением параметра '--bed' является один или несколько путей(разделенных сепаратором ';' или ':') к '.bed' файлам.

```
--bed значение
```

Корректным значением параметра '--sam' является один или несколько путей(разделенных сепаратором ';' или ':') к '.sam' файлам.

```
--sam значение
```
