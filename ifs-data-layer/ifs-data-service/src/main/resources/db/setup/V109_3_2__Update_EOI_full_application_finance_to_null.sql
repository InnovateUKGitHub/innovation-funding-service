-- IFS-2335 - EOI competitions should have a nulled `full_application_finance` column to represent having 'No Finances'.
-- They should also be updated to have a default `max_research_ratio = 0`.

UPDATE competition
SET competition.full_application_finance = NULL
WHERE competition.name = 'Template for the Expression of interest competition type';

UPDATE competition
SET
  competition.full_application_finance = NULL,
  competition.max_research_ratio       = 0
WHERE competition.competition_type_id = (SELECT id
                                         FROM competition_type
                                         WHERE competition_type.name = 'Expression of interest')