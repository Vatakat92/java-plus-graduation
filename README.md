# Explore With Me

Приложение для организации и поиска событий. Состоит из двух сервисов:
- **EWM Service** - основной сервис для работы с событиями
- **Stats Service** - сервис статистики просмотров

## Структура проекта
```
explore-with-me/
├── stat_svc/           # Сервис статистики (multi-module)
│   ├── dto/            # DTO для статистики
│   ├── client/         # Клиент для подключения к stats-service
│   └── service/        # Основной модуль stats-service
└── ewm_service/        # Основной сервис событий
```

## Быстрый старт (Docker)
```bash
# Сборка всех модулей
mvn clean package

# Запуск всех сервисов
docker-compose up -d --build
```

После запуска доступны:
- **EWM Service**: http://localhost:8080
- **Stats Service**: http://localhost:9090
- **EWM DB**: localhost:5433 (postgres/postgres, db: ewm)
- **Stats DB**: localhost:5460 (postgres/postgres, db: stats)

## Локальный запуск

### 1. Запустить базы данных
```bash
# Stats DB
docker run -d \
  --name stats-db \
  -e POSTGRES_DB=stats \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5460:5432 \
  postgres:16.1

# EWM DB
docker run -d \
  --name ewm-db \
  -e POSTGRES_DB=ewm \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  postgres:16.1
```

### 2. Запустить сервисы
```bash
# Сборка
mvn clean install

# Запуск Stats Service
cd stat_svc/service
mvn spring-boot:run

# Запуск EWM Service (в другом терминале)
cd ewm_service
mvn spring-boot:run
```

### Формат дат
Все даты передаются в формате: `yyyy-MM-dd HH:mm:ss` (с пробелом, без `T`)

Пример: `2025-11-10 15:30:00`

### Health Check
```bash
# EWM Service
curl http://localhost:8080/actuator/health

# Stats Service
curl http://localhost:9090/actuator/health
```

## Разработка

### Требования
- Java 21
- Maven 3.8+
- Docker (для запуска в контейнерах)

### Сборка проекта
```bash
# Полная сборка
mvn clean install

# Только компиляция
mvn compile

# Запуск тестов
mvn test

# Пропустить тесты при сборке
mvn clean package -DskipTests
```

### Проверка кода
```bash
# Checkstyle + SpotBugs
mvn verify -P check

# Покрытие тестами
mvn verify -P coverage
```

## Остановка сервисов
```bash
# Docker
docker-compose down

# Удалить volumes (очистить БД)
docker-compose down -v
```

## Архитектура
```
┌─────────────┐         ┌──────────────┐
│   Client    │────────▶│  EWM Service │
└─────────────┘         └──────┬───────┘
                               │
                               │ REST
                               ▼
                        ┌──────────────┐
                        │Stats Service │
                        └──────┬───────┘
                               │
                    ┌──────────┴──────────┐
                    ▼                     ▼
              ┌──────────┐          ┌──────────┐
              │  EWM DB  │          │ Stats DB │
              └──────────┘          └──────────┘
```

## Лицензия

Учебный проект Яндекс.Практикум