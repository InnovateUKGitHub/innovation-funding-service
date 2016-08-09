RENAME TABLE cost_field TO finance_row_meta_field;

ALTER TABLE finance_row_meta_value CHANGE cost_field_id finance_row_meta_field_id BIGINT(20) NOT NULL DEFAULT '0';