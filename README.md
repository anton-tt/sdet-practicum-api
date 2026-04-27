# Cервис API

Данный сервис предоставляет точки доступа для управления сущностями в базе данных PostgreSQL. Включает конфигурацию Docker Compose для настройки сервиса, базы данных и миграций.

## Начало работы

1. Клонируйте данный репозиторий.
2. Установите Docker Desktop (минимум версия 4.4.4) и Docker Compose, если они еще не установлены.

(для работы Docker может потребоваться установка/обновление WSL 2)

## Использование

Для запуска сервиса, базы данных и миграций, перейдите в каталог проекта и выполните:

```make run``` или ```docker-compose up --build -d```


## Точки доступа API

- Создание сущности: POST /api/create
- Удаление сущности: DELETE /api/delete/{id}
- Получение сущности: GET /api/get/{id}
- Получение всех сущностей: GET /api/getAll
- Обновление сущности: PATCH /api/patch/{id}

#### HOST http://localhost:8080
#### SWAGGER документация http://localhost:8080/api/_/docs/swagger/

## Тест-кейсы:
`/docs/test-cases.md`

## Запуск тестов
1. Собрать проект и выполнить тесты Maven:
```bash
mvn clean test
```
2. Сформировать отчет Allure
```bash
allure serve target/allure-results
```
