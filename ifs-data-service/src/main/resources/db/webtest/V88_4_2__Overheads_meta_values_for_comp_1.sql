INSERT INTO finance_row_meta_value (finance_row_id, finance_row_meta_field_id, value)
	SELECT id, 5, 'false' from finance_row where name='overhead' and question_id=29;