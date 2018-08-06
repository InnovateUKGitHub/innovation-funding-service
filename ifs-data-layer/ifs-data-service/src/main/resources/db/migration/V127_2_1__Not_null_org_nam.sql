UPDATE organisation SET name = '' WHERE name IS NULL;
ALTER TABLE organisation MODIFY name VARCHAR(255) NOT NULL;
