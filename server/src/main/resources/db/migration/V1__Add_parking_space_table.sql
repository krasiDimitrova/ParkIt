CREATE TABLE parking_space
(
    id                BIGSERIAL NOT NULL PRIMARY KEY,
    latitude          DECIMAL   NOT NULL,
    longitude         DECIMAL   NOT NULL,
    sensor_identifier CHAR(36)  NOT NULL,
    is_free           BOOLEAN
);
