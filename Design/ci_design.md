# Continuous Integration

Для CI выбран GitHub Actions т.к. он уже интегрирован в GitHub.
Для открытых репозиториев нет ораничений по времени тестов.
Операционная система тестирования - Linux.

CI запускается при:
  * push в любую ветку с изменениям файлов в VariantCaller для которой есть pull request.
  * pull request в master ветку с изменениям файлов в VariantCaller.

## Файлы CI

```
bioinf-practice-2020-variant-caller/.github/workflows/
    gradle.yml
```

## Задачи CI

Assembling and testing:
  * Set up JDK 11 - установка JDK 11
  * Grant execute permission for gradlew - выдать разрешение на запуск gradlew
  * Build project - сборка проекта и запуск юнит тестов
  * Run integration tests - запуск интерационных тестов
  * Run CheckStyle and SpotBugs - запуск анализа кода
  