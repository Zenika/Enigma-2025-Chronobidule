TRUNCATE TABLE stores CASCADE;

ALTER TABLE stores
ADD COLUMN base_url VARCHAR(255) NOT NULL;
