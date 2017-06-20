SELECT @programme_template_comp_id := template_competition_id FROM competition_type WHERE name = 'Programme';
SELECT @feedback_form_input_type_id := id FROM form_input_type WHERE name = 'TEXTAREA';

UPDATE form_input
SET form_input.guidance_title = 'Guidance for assessing economic benefits'
WHERE
  form_input.scope = 'ASSESSMENT' AND
  form_input.form_input_type_id = @feedback_form_input_type_id AND
  form_input.question_id = (SELECT question.id
                            FROM question
                            WHERE question.competition_id = @programme_template_comp_id AND
                                  question.short_name = 'Economic benefit');

UPDATE form_input
SET form_input.guidance_title = 'Guidance for assessing project costs'
WHERE
  form_input.scope = 'ASSESSMENT' AND
  form_input.form_input_type_id = @feedback_form_input_type_id AND
  form_input.question_id = (SELECT question.id
                            FROM question
                            WHERE question.competition_id = @programme_template_comp_id AND
                                  question.short_name = 'Funding');
