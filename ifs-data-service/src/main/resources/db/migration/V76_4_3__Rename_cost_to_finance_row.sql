RENAME TABLE cost TO finance_row;

ALTER TABLE finance_row_meta_value CHANGE cost_id finance_row_id BIGINT(20) NOT NULL DEFAULT '0';