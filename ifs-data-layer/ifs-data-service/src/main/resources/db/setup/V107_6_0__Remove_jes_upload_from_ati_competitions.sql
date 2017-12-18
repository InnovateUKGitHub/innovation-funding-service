-- IFS-2396: As with the APC competition type, jes uploads are not currently required (although this may change)

SET @ati_comp_template_id = (SELECT id FROM competition WHERE name = "Template for the Aerospace Technology Institute competition type");
SET @jes_form_question_id = (SELECT id from question where competition_id = @ati_comp_template_id AND short_name = "Je-s Output");
DELETE FROM form_input WHERE question_id = @jes_form_question_id;
DELETE FROM question_status WHERE question_id = @jes_form_question_id;
DELETE FROM question WHERE id = @jes_form_question_id;


