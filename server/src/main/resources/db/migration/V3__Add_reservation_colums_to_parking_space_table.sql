ALTER TABLE parking_space
    ADD COLUMN reservation_start_timestamp BIGINT;

ALTER TABLE parking_space
    ADD COLUMN reserved_by BIGINT;

ALTER TABLE parking_space
    ADD FOREIGN KEY (reserved_by) REFERENCES parkit_user (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE;
