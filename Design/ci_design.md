# Continuous Integration

Для CI выбран GitHub Actions т.к. он уже интегрирован в GitHub.

CI запускается при:
  * push в любую ветку с изменениям файлов в VariantCaller
  * pull request в master ветку с изменениям файлов в VariantCaller

## Файлы CI

```
~/.github/workflows/
    gradle.yml
```

## Задачи CI

Assembling and testing:
  * Set up JDK 11 - установка JDK 11
  * Grant execute permission for gradlew - выдать разрешение на запуск gradlew
  * Assemble project - сборка проекта
  * Run unit tests - запуск юнит тестов
  * Run integrations test - запуск интерационных тестов