# Поддержка аргументов командной строки

## Выбор билиотеки
Для парсинга агрументов командной строки была выбрана библиотека jopt-simple, поскольку, по нашему мнению, она превосходит другие библиотеки, такие как
(JArgs, Jakarta Commons CLI, TE-Code, argparser, Java port of GNU getopt, Args4J, JSAP, CLAJR, CmdLn, JewelCli, JCommando, parse-cmd, JCommander, plume-lib, Options, picocli) по простоте использования, а также чистоте и ясности кода.

## Примеры параметров и стиль наименования

[Примеры и стиль параметров jopt](http://jopt-simple.github.io/jopt-simple/examples.html)

### Примеры параметров, используемых программой:

Корректным значением параметра '-fp' является один путь к '.fasta' файлу.

```
-fp значение
```

Корректным значением параметра '-bp' является один или несколько путей(разделенных сепаратором ';' или ':') к '.bed' файлам.

```
-bp значение
```

Корректным значением параметра '-sp' является один или несколько путей(разделенных сепаратором ';' или ':') к '.sam' файлам.

```
-sp значение
```

## Пакеты и классы программы

В пакете main/java/com/epam/bioinf/variantcaller находятся основные пакеты программы(parsers и utils), а также файл Main и Launcher.

* Пакет parsers содержит в себе класс CommandLineParser, отвечающий за парсинг аргументов из командной строки(в будущем планируется использовать этот пакет для хранения классов, отвечающих за парсинг информации из '.fasta', '.bed' и '.sam' файлов).

* Пакет utils содержит в себе класс ExcConstants, отвечающий за хранения константных сообщений об ошибках, используемых классом CommandLineParser.

* Класс Main создает экземпляр Launcher и передает ему в метод start массив аргументов, полученных из командной строки.

* Класс Launcher вызывает метод CommandLineParser parse, сохраняет результат и возвращает 0, если парсинг прошел успешно, или 1 в случае ошибки.

## Сигнатуры и описания public методов класса CommandLineParser

```
parse(String[] args)
```

Данный метод отвечает за валидацию аргументов, переданных ему в параметре, а также делегирует сам процесс парсинга объекту parser класса OptionParser(ссылка на который содержится в полях объекта CommandLineParser), который возвращает аргументы, полученные в соответствии с параметрами parser. Сами параметры parser выставляются в конструкторе объекта-оболочки - CommandLineParser.

```
getOptions()
```
Данный метод возвращает объект OptionSet, который содержит в себе аргументы, спарсенные в соответствии с параметрами parser, а также эти параметры. Метод используется в unit-тесте CommandLineParserTest.

```
getFastaPath()
```

Данный метод возвращает список из одного '.fasta' файла, полученного из переданного методу parse значения параметра -fp, либо пустой список если парсеру еще не удалось получить значение. Метод используется в Launcher для сохранения '.fasta' файла, полученного после парсинга.

```
getBedPaths()
```

Данный метод возвращает список из одного или нескольких '.bed' файлов, полученных из переданного методу parse значения параметра -bp, либо пустой список если парсеру еще не удалось получить значения. Метод используется в Launcher для сохранения '.bed' файлов, полученных после парсинга.

```
getSamPaths()
```

Данный метод возвращает список из одного или нескольких '.sam' файлов, полученных из переданного методу parse значения параметра -bp, либо пустой список если парсеру еще не удалось получить значения. Метод используется в Launcher для сохранения '.sam' файлов, полученных после парсинга.

## Сигнатуры и описания public методов класса Launcher

```
start(String[] args)
```

Данный метод отвечает за передачу параметров парсеру, а также сохранение результатов парсинга. Возвращает статус 0 в случае успеха или 1 при ошибке.

```
getResultFp()
```
Данный метод возвращает '.fasta' файл, полученный после парсинга(либо null если метод start не был вызван или произошла ошибка в ходе парсинга).

```
getResultBp()
```

Данный метод возвращает '.bed' файлы, полученные после парсинга(либо null если метод start не был вызван или произошла ошибка в ходе парсинга).

```
getResultSp()
```

Данный метод возвращает '.sam' файлы, полученные после парсинга(либо null если метод start не был вызван или произошла ошибка в ходе парсинга).


## Таблица тестов для класса CommandLineParser

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|parserMustAcceptValidParameters|Тест проверяет, что настройки CommandLineParser по допустимым аргументам были выставлены правильно|
|2|parserMustFailIfInvalidParameters|Тест проверяет, что CommandLineParser бросает OptionException, при передаче недопустимых параметров|
|3|parserMustAcceptMultipleArguments|Тест проверяет, что может принимать несколько значений параметров -bp и -sp|
|4|parserMustFailIfMoreThanOneFastaPathProvided|Тест проверяет, что CommandLineParser бросает OptionException при передаче нескольких значений параметра -fp|
|5|parserMustFailIfLessThanOneFastaPathProvided|Тест проверяет, что CommandLineParser бросает OptionException при отсутствии значений параметра -fp|
|6|parserMustFailIfLessThanOneBedPathProvided|Тест проверяет, что CommandLineParser бросает OptionException при отсутствии значений параметра -bp|
|7|parserMustFailIfLessThanOneSamPathProvided|Тест проверяет, что CommandLineParser бросает OptionException при отсутствии значений параметра -sp|
|8|parserMustRemoveDuplicatedPaths|Тест проверяет, что CommandLineParser корректно удаляет дупликаты путей к файлам|
|9|parserMustFailIfFastaPathHasInvalidExtension|Тест проверяет, что CommandLineParser бросает OptionException, если значение параметра -fp имеет не '.fasta' расширение|
|10|parserMustFailIfSomeBedPathHasInvalidExtension|Тест проверяет, что CommandLineParser бросает OptionException, если хоть одно значение параметра -bp имеет не '.bed' расширение|
|11|parserMustFailIfSomeSamPathHasInvalidExtension|Тест проверяет, что CommandLineParser бросает OptionException, если хоть одно значение параметра -sp имеет не '.sam' расширение|
|12|parserMustFailIfFastaFileDoesNotExist|Тест проверяет, что CommandLineParser бросает OptionException, если путь, указанный в параметре -fp, не представляет собой путь к существующему '.fasta' файлу|
|13|parserMustFailIfSomeBedFileDoesNotExist|Тест проверяет, что CommandLineParser бросает OptionException, если хоть один из путей, указанных в параметре -bp, не представляет собой путь к существующему '.bed' файлу|
|14|parserMustFailIfSomeSamFileDoesNotExist|Тест проверяет, что CommandLineParser бросает OptionException, если хоть один из путей, указанных в параметре -sp, не представляет собой путь к существующему '.sam' файлу|

## Таблица тестов для класса Launcher

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|launcherMustReturnZeroWithValidParameters|Тест проверяет, что экземпляр Launcher вернул 0 в при верных параметрах|
|2|launcherMustReturnOneWithInvalidParameters|Тест проверяет, что экземпляр Launcher вернул 1 в при неверных параметрах|
|3|launcherMustReturnNullIfResultFilesRequestedWithInvalidParameters|Тест проверяет, что экземпляр Launcher вернул null для всех результатов при неверных параметрах|

## Таблица интеграционных тестов программы

|Номер теста|Название теста|Описание теста|
|---|---|---|
|1|programMustWorkWithCorrectArguments|Тест проверяет, что экземпляр Launcher вернул 0 в при верных параметрах и результаты, полученные CommandLineParser соответствуют ожидаемым|
|2|programMustWorkWithInvalidArguments|Тест проверяет, что экземпляр Launcher вернул 1 в при неверных параметрах, а результаты не были получены|



