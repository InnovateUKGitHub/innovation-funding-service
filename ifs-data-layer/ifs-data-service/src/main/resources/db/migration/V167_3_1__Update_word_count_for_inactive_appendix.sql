-- IFS-7310:Allow applicants to upload multiple appendices- Comp setup journey
UPDATE form_input fi
SET word_count = 0
WHERE form_input_type_id = 4 AND active = 0;