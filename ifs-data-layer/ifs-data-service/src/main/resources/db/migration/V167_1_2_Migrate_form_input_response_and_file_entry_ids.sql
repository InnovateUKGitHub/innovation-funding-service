--IFS-7309 - Migration of form_input_response_id and file_entry_id to form_input_file_entry table
INSERT INTO form_input_file_entry(form_input_response_id, file_entry_id)
SELECT fir.id, file_entry_id
FROM file_entry fe
INNER JOIN form_input_response fir ON fe.id = fir.file_entry_id;