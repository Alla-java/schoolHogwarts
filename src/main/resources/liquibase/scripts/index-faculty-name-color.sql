-- liquibase formatted sql

-- changeset azatsepina:2
CREATE INDEX idx_name_color ON faculty(name, color);