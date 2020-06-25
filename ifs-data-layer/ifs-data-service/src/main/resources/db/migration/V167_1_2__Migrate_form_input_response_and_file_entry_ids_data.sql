-- IFS-7309 - Migration of form_input_response_id and file_entry_id to form_input_file_entry table and set word_count to 1 for FILEUPLOAD input type.
INSERT INTO form_input_response_file_entry(form_input_response_id, file_entry_id)
SELECT fir.id, file_entry_id
FROM file_entry fe
INNER JOIN form_input_response fir ON fe.id = fir.file_entry_id;

UPDATE form_input
SET word_count = 1
WHERE form_input_type_id = 4;
