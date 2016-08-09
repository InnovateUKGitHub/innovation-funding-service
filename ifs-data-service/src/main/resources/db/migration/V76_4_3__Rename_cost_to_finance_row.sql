RENAME TABLE cost TO finance_row;

ALTER TABLE finance_row_meta_value DROP FOREIGN KEY `FK_cryaaiuibh4b0sqw3aqrkspmb`;
ALTER TABLE finance_row_meta_value CHANGE cost_id finance_row_id BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE finance_row_meta_value ADD CONSTRAINT FK_cryaaiuibh4b0sqw3aqrkspmb FOREIGN KEY (finance_row_id) REFERENCES finance_row (id);