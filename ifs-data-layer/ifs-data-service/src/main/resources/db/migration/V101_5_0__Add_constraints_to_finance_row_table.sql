-- add constraints to finance_row to prevent duplicates
SET @alterTable =
  if( (SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
       WHERE
        constraint_schema = database() AND table_name = 'finance_row' AND
        constraint_name = 'finance_row_application_row_id_fk' AND constraint_type = 'FOREIGN KEY'),

    'SELECT 1',

    'ALTER TABLE finance_row
       ADD CONSTRAINT row_type_application_row_unique UNIQUE (row_type, application_row_id),
       ADD CONSTRAINT finance_row_application_row_id_fk FOREIGN KEY (application_row_id) REFERENCES finance_row(id),
       ADD INDEX finance_row_application_row_id_idx (id);'
  );

PREPARE alterTableStatement from @alterTable;
EXECUTE alterTableStatement;
DEALLOCATE PREPARE alterTableStatement;
