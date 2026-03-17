# Explore With Me (EWM)

Платформа для публикации мероприятий и участия в них. Пользователи могут создавать события, подавать заявки на участие, оставлять комментарии, а администраторы — управлять каталогом категорий и подборками.

---

## Содержание

- [Архитектура](#архитектура)
  - [Инфраструктурные сервисы](#инфраструктурные-сервисы)
  - [Сервис статистики](#сервис-статистики)
  - [Основные сервисы](#основные-сервисы)
  - [Схема взаимодействия](#схема-взаимодействия)
  - [Конфигурации](#конфигурации)
- [Внутренний API](#внутренний-api)
- [Внешний API](#внешний-api)

---

## Архитектура

Проект построен по принципу **микросервисной архитектуры**. Все сервисы регистрируются в Eureka Discovery Server и получают централизованную конфигурацию через Config Server. Внешние запросы поступают через единственную точку входа — Gateway Server.

### Инфраструктурные сервисы

| Сервис | Модуль | Порт | Описание |
|---|---|---|---|
| **Discovery Server** | `infra/discovery-server` | `8761` | Реестр сервисов на базе Netflix Eureka. Все микросервисы регистрируются здесь и находят друг друга по имени. |
| **Config Server** | `infra/config-server` | динамический | Централизованное хранилище конфигураций (Spring Cloud Config, native profile). При старте раздаёт настройки каждому сервису. |
| **Gateway Server** | `infra/gateway-server` | `8080` | Единая точка входа (Spring Cloud Gateway). Маршрутизирует запросы по путям к нужным сервисам через балансировщик нагрузки (`lb://`). |

### Сервис статистики

| Сервис | Модуль | Порт | База данных |
|---|---|---|---|
| **Stats Server** | `stats/stats-server` | `9090` | PostgreSQL, порт `6543` (контейнер `ewm-stats-db`) |

Вспомогательные модули:
- `stats/stats-dto` — общие DTO (`EndpointHitDto`, `ViewStatsDto`), используемые и сервером, и клиентом.
- `stats/stats-client` — HTTP-клиент (`StatsClient`) для отправки hits и получения статистики из Stats Server.

### Основные сервисы

Все основные сервисы запускаются на **динамическом порту** (`server.port: 0`) и регистрируются в Eureka. Межсервисное взаимодействие реализовано через **OpenFeign** с Circuit Breaker на базе **Resilience4j**.

| Сервис | Модуль | База данных (хост-порт) |
|---|---|---|
| **User Service** | `core/user-service` | PostgreSQL `5440` (контейнер `user-service-db`) |
| **Category Service** | `core/category-service` | PostgreSQL `5435` (контейнер `category-service-db`) |
| **Event Service** | `core/event-service` | PostgreSQL `5438` (контейнер `event-service-db`) |
| **Request Service** | `core/request-service` | PostgreSQL `5439` (контейнер `request-service-db`) |
| **Comment Service** | `core/comment-service` | PostgreSQL `5436` (контейнер `comment-service-db`) |
| **Compilation Service** | `core/compilation-service` | PostgreSQL `5437` (контейнер `compilation-service-db`) |

### Схема взаимодействия

```
Клиент
  │
  ▼
Gateway Server (:8080)
  │
  ├──► User Service          ◄──── Event Service, Comment Service, Request Service
  ├──► Category Service      ◄──── Event Service
  ├──► Event Service         ◄──── Category Service, User Service, Request Service, Stats Server
  ├──► Request Service       ◄──── Event Service, User Service
  ├──► Comment Service       ◄──── Event Service, User Service, Request Service
  ├──► Compilation Service   ◄──── Event Service, Request Service, Stats Server
  └──► Stats Server (:9090)
```

Все сервисы при старте обращаются к **Config Server** за конфигурацией и регистрируются в **Discovery Server**.

### Конфигурации

Конфигурации сервисов хранятся в Config Server и расположены по пути:

```
infra/config-server/src/main/resources/config/
├── category-service/    # category-service.yaml
├── comment-service/     # comment-service.yaml
├── compilation-service/ # compilation-service.yaml
├── event-service/       # event-service.yaml
├── gateway-server/      # gateway-server.yaml  (маршруты Gateway)
├── request-service/     # request-service.yaml
├── stats-server/        # stats-server.yaml
└── user-service/        # user-service.yaml
```

Каждый файл содержит настройки базы данных, параметры Circuit Breaker / TimeLimiter для Feign-клиентов и уровни логирования. Локальные `application.yaml` сервисов содержат только минимум, необходимый для подключения к Config Server и Eureka.

---

## Внутренний API

Внутренние эндпоинты предназначены исключительно для межсервисного взаимодействия и недоступны напрямую для внешних клиентов (трафик также маршрутизируется через Gateway по путям `/internal/**`).

---

### User Service — `/internal/users`

**Получить пользователя по ID**
```
GET /internal/users/by-id/{userId}
```
Ответ: `UserShortDto { id, name }`

---

**Получить пользователей по списку ID**
```
POST /internal/users/by-ids
Body: { "ids": [1, 2, 3] }
```
Ответ: `Map<Long, UserShortDto>`

---

### Category Service — `/internal/category`

**Получить категорию по ID**
```
GET /internal/category/by-id/{categoryId}
```
Ответ: `CategoryShortDto { id, name }`

---

**Получить категории по списку ID**
```
POST /internal/category/by-ids
Body: { "ids": [1, 2, 3] }
```
Ответ: `Map<Long, CategoryShortDto>`

---

### Event Service — `/internal/events`

**Получить событие по ID**
```
GET /internal/events/by-id/{eventId}
```
Ответ: `EventSummaryDto { id, title, initiatorId, categoryId, eventDate, state, participantLimit, requestModeration }`

---

**Проверить существование событий в категории**
```
GET /internal/events/by-category-id/{categoryId}
```
Ответ: `Boolean`

Используется в Category Service перед удалением категории, чтобы убедиться в отсутствии привязанных событий.

---

**Получить события по списку ID**
```
POST /internal/events/by-ids
Body: { "ids": [1, 2, 3] }
```
Ответ: `List<EventSummaryDto>`

---

### Request Service — `/internal/requests`

**Получить количество заявок по списку событий и статусу**
```
POST /internal/requests/count-by-ids
Body: { "eventIds": [1, 2, 3], "status": "CONFIRMED" }
```
Ответ: `Map<Long, Long>` — маппинг `eventId → количество заявок`

---

**Получить количество заявок по одному событию и статусу**
```
GET /internal/requests/count-by-id/{eventId}/status/{status}
```
Ответ: `Long`

---

**Получить заявку конкретного пользователя на конкретное событие**
```
GET /internal/requests/userid/{userId}/eventid/{eventId}
```
Ответ: `RequestShortDto { id, requesterId, eventId, status }`

Используется в Comment Service для проверки, является ли пользователь участником события (что даёт право на комментирование).

---

### Сводная таблица зависимостей сервисов

| Сервис | Использует внутренний API |
|---|---|
| **Event Service** | Category Service, User Service, Request Service, Stats Server |
| **Category Service** | Event Service (проверка привязанных событий) |
| **Request Service** | Event Service, User Service |
| **Comment Service** | Event Service, User Service, Request Service |
| **Compilation Service** | Event Service, Request Service, Stats Server |

---

## Внешний API

Спецификации внешнего API в формате OpenAPI (Swagger) находятся в корне проекта:

| Файл | Описание |
|---|---|
| [`ewm-main-service-spec.json`](ewm-main-service-spec.json) | API основного сервиса: события, категории, заявки, подборки, комментарии. Включает разделы для публичного доступа (`/events`, `/categories`, `/compilations`), приватного доступа пользователей (`/users/**`) и административного доступа (`/admin/**`). |
| [`ewm-stats-service-spec.json`](ewm-stats-service-spec.json) | API сервиса статистики: сохранение обращений к эндпоинтам (`POST /hit`) и получение агрегированной статистики (`GET /stats`). |

Для просмотра спецификации можно воспользоваться [Swagger Editor](https://editor.swagger.io/) — вставить содержимое нужного JSON-файла.
