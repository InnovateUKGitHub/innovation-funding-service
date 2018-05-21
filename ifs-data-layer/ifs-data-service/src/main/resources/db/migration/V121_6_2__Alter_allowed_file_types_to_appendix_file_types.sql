--IFS-2959 Convert allowed_file_types values to rows in the appendix_file_types table

INSERT INTO appendix_file_types(form_input_id, type)
    SELECT f.id, 'PDF'
  FROM form_input f
  WHERE allowed_file_types LIKE '%PDF%';


INSERT INTO appendix_file_types(form_input_id, type)
    SELECT f.id, 'SPREADSHEET'
  FROM form_input f
  WHERE allowed_file_types LIKE '%SPREADSHEET%';