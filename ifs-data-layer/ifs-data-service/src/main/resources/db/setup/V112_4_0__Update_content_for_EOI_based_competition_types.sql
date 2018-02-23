-- IFS-2784 - Update content in competition types that were based off EOI.

-- Update `section` descriptions
UPDATE section
  INNER JOIN competition
    ON section.competition_id = competition.id
  INNER JOIN competition_type
    ON (competition.id = competition_type.template_competition_id
        OR competition.competition_type_id = competition_type.id)
      AND competition_type.name IN ('Expression of interest', 'Advanced Propulsion Centre', 'Aerospace Technology Institute', 'The Prince\'s Trust')
SET section.description = 'Please provide information about your project. This section is not scored but will provide background to the project.'
WHERE section.name = 'Project details';

UPDATE section
  INNER JOIN competition
    ON section.competition_id = competition.id
  INNER JOIN competition_type
    ON (competition.id = competition_type.template_competition_id
        OR competition.competition_type_id = competition_type.id)
       AND competition_type.name IN ('Expression of interest', 'Advanced Propulsion Centre', 'Aerospace Technology Institute', 'The Prince\'s Trust')
SET section.description = 'These are the questions which will be marked by the assessors.'
WHERE section.name = 'Application questions';
