-- fix the case of category.NAME
ALTER TABLE category CHANGE NAME name VARCHAR(255) NOT NULL;