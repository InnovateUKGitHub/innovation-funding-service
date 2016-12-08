ALTER TABLE finance_row_meta_value DROP FOREIGN KEY `FK_cryaaiuibh4b0sqw3aqrkspmb`;

ALTER TABLE finance_row_meta_value DROP FOREIGN KEY `FK_h6lijwiwnsblqurwxjftvdn7n`;

ALTER TABLE finance_row_meta_value DROP PRIMARY KEY;

ALTER TABLE finance_row_meta_value ADD `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT FIRST;

ALTER TABLE finance_row_meta_value ADD CONSTRAINT FK_cryaaiuibh4b0sqw3aqrkspmb FOREIGN KEY (finance_row_id) REFERENCES finance_row (id);

ALTER TABLE finance_row_meta_value ADD CONSTRAINT FK_h6lijwiwnsblqurwxjftvdn7n FOREIGN KEY (finance_row_meta_field_id) REFERENCES finance_row_meta_field (id);