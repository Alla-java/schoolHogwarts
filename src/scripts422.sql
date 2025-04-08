-- Создаем таблицу Люди
CREATE TABLE person (
    id SERIAL PRIMARY KEY,  -- уникальный идентификатор
    name VARCHAR(255) NOT NULL,            -- имя человека
    age INT NOT NULL,                     -- возраст человека
    has_license BOOLEAN NOT NULL          -- признак наличия прав
);

-- Создаем таблицу для машин
CREATE TABLE car (
    id SERIAL PRIMARY KEY,                  -- уникальный идентификатор
    brand VARCHAR(255) NOT NULL,             -- марка машины
    model VARCHAR(255) NOT NULL,             -- модель машины
    cost DECIMAL(10, 2) NOT NULL            -- стоимость машины
);

-- Таблица для связи людей и машин (many-to-many)
CREATE TABLE person_car (
    person_id INT,                          -- ссылка на человека
    car_id INT,                             -- ссылка на машину
    PRIMARY KEY (person_id, car_id),        -- составной первичный ключ
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE,  -- внешний ключ на таблицу person
    FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE CASCADE         -- внешний ключ на таблицу car
);
