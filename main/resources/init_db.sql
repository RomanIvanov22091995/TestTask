-- Создание таблицы USER
CREATE TABLE IF NOT EXISTS "USER"
(
    ID            BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    NAME          VARCHAR(500),
    DATE_OF_BIRTH DATE,
    PASSWORD      VARCHAR(500) CHECK (LENGTH(PASSWORD) >= 8 AND LENGTH(PASSWORD) <= 500)
);

-- Создание таблицы ACCOUNT
CREATE TABLE IF NOT EXISTS "ACCOUNT"
(
    ID      BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    USER_ID BIGINT         NOT NULL UNIQUE REFERENCES "USER" (ID) ON DELETE CASCADE,
    BALANCE DECIMAL(10, 2) NOT NULL CHECK (BALANCE >= 0)
);

-- Создание таблицы EMAIL_DATA
CREATE TABLE IF NOT EXISTS "EMAIL_DATA"
(
    ID      BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    USER_ID BIGINT REFERENCES "USER" (ID) ON DELETE CASCADE,
    EMAIL   VARCHAR(200) UNIQUE NOT NULL
);

-- Создание таблицы PHONE_DATA
CREATE TABLE IF NOT EXISTS "PHONE_DATA"
(
    ID      BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    USER_ID BIGINT REFERENCES "USER" (ID) ON DELETE CASCADE,
    PHONE   VARCHAR(13) UNIQUE NOT NULL CHECK (PHONE ~ '^79[0-9]{9}$')
);

-- Пример вставки данных в таблицу USER
INSERT INTO "USER" (NAME, DATE_OF_BIRTH, PASSWORD)
VALUES ('Рома Силыч', '1993-05-01', 'password123'),
       ('Джейсэм Стетхэм', '1990-10-15', 'securepassword');

-- Пример вставки данных в таблицу ACCOUNT
INSERT INTO "ACCOUNT" (USER_ID, BALANCE)
VALUES (1, 1000.00),
       (2, 2000.50);

-- Пример вставки данных в таблицу EMAIL_DATA
INSERT INTO "EMAIL_DATA" (USER_ID, EMAIL)
VALUES (1, 'john@example.com'),
       (2, 'jane@example.com');

-- Пример вставки данных в таблицу PHONE_DATA
INSERT INTO "PHONE_DATA" (USER_ID, PHONE)
VALUES (1, '79207865432'),
       (2, '79123456789');