-- PLEASE NOTE: This should be safe to remove BEFORE the next run of GenerateTestData since these changes have also been made in the Programme competition template. (Ticket ref INFUND-8823)

SELECT @production_comp_id := id FROM competition WHERE name = 'Connected digital additive manufacturing';
SELECT @feedback_form_input_type_id := id FROM form_input_type WHERE name = 'TEXTAREA';

UPDATE form_input
SET form_input.guidance_title = 'Guidance for assessing economic benefits'
WHERE
  form_input.scope = 'ASSESSMENT' AND
  form_input.form_input_type_id = @feedback_form_input_type_id AND
  form_input.question_id IN (SELECT question.id
                             FROM question
                               INNER JOIN competition
                                 ON question.competition_id = competition.id
                               INNER JOIN competition_type
                                 ON competition.competition_type_id = competition_type.id
                             WHERE competition.id != @production_comp_id AND
                                   competition.template = 0 AND
                                   competition_type.name = 'Programme' AND
                                   question.short_name = 'Economic benefit');

UPDATE form_input
SET form_input.guidance_title = 'Guidance for assessing project costs'
WHERE
  form_input.scope = 'ASSESSMENT' AND
  form_input.form_input_type_id = @feedback_form_input_type_id AND
  form_input.question_id IN (SELECT question.id
                             FROM question
                               INNER JOIN competition
                                 ON question.competition_id = competition.id
                               INNER JOIN competition_type
                                 ON competition.competition_type_id = competition_type.id
                             WHERE competition.id != @production_comp_id AND
                                   competition.template = 0 AND
                                   competition_type.name = 'Programme' AND
                                   question.short_name = 'Funding');