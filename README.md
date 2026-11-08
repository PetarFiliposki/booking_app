# Termin: Online Booking Platform

REST API за резервација на термини за мали бизниси (фризерски салони, сервисери, теретани).
Сопственикот дефинира услуги и работно време, а клиентите резервираат слободни термини
без телефонски повици.

## Функции

- Регистрација и најава со **JWT**, улоги `CUSTOMER`, `BUSINESS_OWNER`, `ADMIN`
- Бизниси и услуги (име, траење, цена) со проверка на сопственост
- Работно време по ден од неделата
- Пресметка на слободни термини според работно време, траење на услуга и постоечки резервации
- Резервации со **гаранција против двојна резервација** на ниво на база
- Откажување со правило: клиентот не може да откаже помалку од 2 часа пред терминот
- Јасни HTTP статуси и JSON грешки (400, 401, 403, 404, 409)

## Технологии

| Област | Технологија |
|---|---|
| Јазик | Java 21 |
| Framework | Spring Boot, Spring Web, Spring Security, Spring Data JPA (Hibernate) |
| База | PostgreSQL 16, Flyway миграции |
| Автентикација | JWT (jjwt), BCrypt |
| Тестови | JUnit 5, Mockito, Testcontainers |
| DevOps | Docker, Docker Compose, GitHub Actions |
| Документација | OpenAPI (`/v3/api-docs`) |

## Стартување

Потребно: Docker. За развој и тестови уште и JDK 21.

### Опција 1: сè во Docker

```bash
docker compose up -d --build
```

Backend: http://localhost:8080, проверка: http://localhost:8080/api/ping

### Опција 2: база во Docker, backend локално

```bash
docker compose up -d db
cd backend
./mvnw spring-boot:run
```

PostgreSQL е достапен на порта **5433** (за да не се судри со локален PostgreSQL).

### Конфигурација (променливи на околина)

| Променлива | Опис | Default (само за развој) |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC адреса | `jdbc:postgresql://localhost:5433/booking` |
| `SPRING_DATASOURCE_USERNAME` | Корисник | `booking` |
| `SPRING_DATASOURCE_PASSWORD` | Лозинка | `booking` |
| `JWT_SECRET` | Тајна за потпис (најмалку 32 знаци) | dev вредност |

Во продукција задолжително поставете сопствена `JWT_SECRET`.

### Тестови

```bash
cd backend
./mvnw test
```

Integration тестовите користат Testcontainers (бара Docker). CI ги пушта автоматски
на секој push.

## API

| Метод | Адреса | Опис | Пристап |
|---|---|---|---|
| POST | `/api/auth/register` | Регистрација | јавно |
| POST | `/api/auth/login` | Најава, враќа JWT | јавно |
| GET | `/api/users/me` | Мој профил | најавен |
| GET | `/api/businesses` | Листа на бизниси | јавно |
| GET | `/api/businesses/{id}` | Еден бизнис | јавно |
| POST | `/api/businesses` | Нов бизнис | BUSINESS_OWNER |
| GET | `/api/businesses/{id}/services` | Услуги | јавно |
| POST | `/api/businesses/{id}/services` | Нова услуга | сопственик |
| GET | `/api/businesses/{id}/working-hours` | Работно време | јавно |
| PUT | `/api/businesses/{id}/working-hours` | Постави работно време за ден | сопственик |
| GET | `/api/businesses/{id}/availability?serviceId=&date=` | Слободни термини | јавно |
| POST | `/api/businesses/{id}/bookings` | Резервација | најавен |
| GET | `/api/bookings/my` | Мои резервации | најавен |
| POST | `/api/bookings/{id}/cancel` | Откажување | клиент или сопственик |
