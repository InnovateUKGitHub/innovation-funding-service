/**
  - V107_3_2__Update_existing_form_inputs_having_appendix_with_allowed_file_types.sql (see IFS-2285)
  - duplicated here in order to catch any nulled allowed_file_types during ZDD (see IFS-2425)
**/

SET @file_upload_id = (SELECT id FROM form_input_type WHERE name = 'FILEUPLOAD');
UPDATE form_input SET allowed_file_types = 'PDF' WHERE form_input_type_id = @file_upload_id;

