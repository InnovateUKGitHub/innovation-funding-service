/**
  - V105_6_1__Insert_column_to_store_allowed_file_types_to_form_input.sql adds a new field allowed file types (see IFS-2285)
  - This patch sets existing form inputs allowing appendix to allow PDF as that has been only allowed type this far.
**/

SET @file_upload_id = (SELECT id FROM form_input_type WHERE name = 'FILEUPLOAD');
UPDATE form_input SET allowed_file_types = 'PDF' WHERE form_input_type_id = @file_upload_id;

