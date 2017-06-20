-- Insert new meta field for 'use total'
INSERT INTO finance_row_meta_field (`title`, `type`) VALUES ('use_total', 'Boolean');

-- Get id for meta field that was just inserted
SET @use_total_meta_field_id = (SELECT id FROM finance_row_meta_field WHERE title='use_total');

-- Tag all currently existing overheads with use_total as false
INSERT INTO finance_row_meta_value (finance_row_id, finance_row_meta_field_id, value)
SELECT id, @use_total_meta_field_id, 'false' FROM finance_row WHERE name='overhead' AND row_type='ApplicationFinanceRow';

-- Insert new meta field for 'file entry'
INSERT INTO finance_row_meta_field (`title`, `type`) VALUES ('file_entry', 'String');