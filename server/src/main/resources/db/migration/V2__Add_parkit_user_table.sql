CREATE TABLE parkit_user
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    email      CHAR(256) NOT NULL UNIQUE,
    first_name CHAR(50)  NOT NULL,
    last_name  CHAR(50)  NOT NULL,
    password   VARCHAR(60)  NOT NULL
);
