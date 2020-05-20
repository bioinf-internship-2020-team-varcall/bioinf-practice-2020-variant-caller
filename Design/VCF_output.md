# VCF Вывод.
## Используемая библиотека

Используется библиотека [htsjdk](https://github.com/samtools/htsjdk), поскольку она является широко используемой Java библиотекой для работы с форматами данных биоинформатики и предоставляет удобный api.

### Класс VCFHandler
```java
package com.epam.bioinf.variantcaller.handlers;

public class VCFHandler(Path outputFilePath, List<Variant> variants) {
	List<VariantContext> variantContexts;
	
	VCFHandler() {
		VariantContextWriter writer = configureVcfWriter(outputFilePath);
		VCFHeader header = getHeader();
		variantContexts = getVariantContexts(variants);
		for (VariantContext vc : variantContexts) {
			writer.add(vc);
		}
	}
	
	private VariantContextWriter configureVcfWriter(Path outputFilePath) {
		//возвращает VariantContextWriter с выходным файлом полученным из ParsedArgs.
	}
	
	private VCFHeader getHeader() {
		//возвращает VCFHeader с кратким описанием.
		//Возможно добавление SequenceDictionary.
	}

	private List<VariantContext> getVariantContexts(List<Variant> variants) {
		//точная структура данных variants пока не известна.
		//создает List<VariantContext> итерируя по variants превращая их в VariantContext через VariantContextBuilder
	}
}
```
Unit-тесты:
  * Vcf must be writtenif valid variants provided
  * Must return correct header
  * Must return correct variant contexts with valid variants
  * Must fail with VcfInvalidVariantsProvidedException if invalid variants provided
  
### VcfInvalidVariantsProvidedException

```java
public class VcfInvalidVariantsProvidedException extends VcfHandlerException {

  // invokes super class constructor
  private static final String INTERVAL_END = "Invalid variants were provided";
}
```

```java
public class VcfHandlerException extends HandlerException {
  // invokes super class constructor
}
```
  

### Изменения в CommandLineParser
```java
package com.epam.bioinf.variantcaller.cmdline;

// Описаны только добавления к уже существующей реализации
public class CommandLineParser {

  private final String OUTPUT_KEY = "output"; // Ключ для выходного файла

//опциональный параметр
  OptionParser optionParser = new OptionParser() {
      {
        accepts(OUTPUT_KEY); 
      }
  };

  OptionSpec<Path> output =
  		getOptionSpecPathsByParameter(optionParser, OUTPUT_KEY);
  parsedArguments = new ParsedArguments(
    options.valuesOf(output)
  );

}
```
* Обновление всех unit-тестов

### Изменения в ParsedArguments
```java
package com.epam.bioinf.variantcaller.cmdline;

// Описаны только добавления к уже существующей реализации
public class ParsedArguments {

  private final Path outputPath;

  public Path getOutputPath() {
      return outputPath;
  }
}
```
* Обновление unit-тестов
