-- IFS-2694 - Remove Scope and Project Summary questions from The Prince's Trust competition type that was
-- added in patch V110_3_0 (IFS-2688).

SET @princes_trust_competition_id = (SELECT id
                                     FROM competition
                                     WHERE id = (SELECT template_competition_id
                                                 FROM competition_type
                                                 WHERE competition_type.name = 'The Prince\'s Trust'));

SET @princes_trust_competition_type_id = (SELECT id
                                          FROM competition_type
                                          WHERE competition_type.name = 'The Prince\'s Trust');

DELETE form_input_validator FROM form_input_validator
  INNER JOIN form_input
    ON form_input_validator.form_input_id = form_input.id
  INNER JOIN question
    ON question.id = form_input.question_id
       AND (question.short_name = 'Project summary' OR question.short_name = 'Scope')
  INNER JOIN competition
    ON form_input.competition_id = competition.id
       AND (competition.id = @princes_trust_competition_id
            OR competition.competition_type_id = @princes_trust_competition_type_id);

DELETE guidance_row FROM guidance_row
  INNER JOIN form_input
    ON guidance_row.form_input_id = form_input.id
  INNER JOIN question
    ON form_input.question_id = question.id
       AND (question.short_name = 'Project summary' OR question.short_name = 'Scope')
  INNER JOIN competition
    ON form_input.competition_id = competition.id
       AND (competition.id = @princes_trust_competition_id
            OR competition.competition_type_id = @princes_trust_competition_type_id);

DELETE form_input FROM form_input
  INNER JOIN question
    ON question.id = form_input.question_id
       AND (question.short_name = 'Project summary' OR question.short_name = 'Scope')
  INNER JOIN competition
    ON form_input.competition_id = competition.id
       AND (competition.id = @princes_trust_competition_id
            OR competition.competition_type_id = @princes_trust_competition_type_id);

DELETE question FROM question
  INNER JOIN competition
    ON question.competition_id = competition.id
       AND (competition.id = @princes_trust_competition_id
            OR competition.competition_type_id = @princes_trust_competition_type_id)
WHERE question.short_name = 'Project summary' OR question.short_name = 'Scope';
