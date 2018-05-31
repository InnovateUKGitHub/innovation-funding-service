-- IFS-2002 Applicant guidance for generic competition Application questions section

UPDATE section
  INNER JOIN competition
    ON section.competition_id = competition.id
  INNER JOIN competition_type
    ON (competition.competition_type_id = competition_type.id
      OR competition.id = competition_type.template_competition_id)
    AND competition_type.name = 'Generic'
SET section.description = 'These are the questions which will be marked by the assessors.'
  WHERE section.name = 'Application questions';
