RENAME TABLE cost_field TO finance_row_meta_field;

ALTER TABLE finance_row_meta_value DROP FOREIGN KEY `FK_h6lijwiwnsblqurwxjftvdn7n`;
ALTER TABLE finance_row_meta_value CHANGE cost_field_id finance_row_meta_field_id BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE finance_row_meta_value ADD CONSTRAINT FK_h6lijwiwnsblqurwxjftvdn7n FOREIGN KEY (finance_row_meta_field_id) REFERENCES finance_row_meta_field (id);