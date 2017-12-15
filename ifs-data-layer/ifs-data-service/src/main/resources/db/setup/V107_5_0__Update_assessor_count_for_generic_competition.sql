-- IFS-1527 Generic competitions assessors count doesn't show the selected assessors value

UPDATE assessor_count_option ac JOIN competition_type ct ON (ac.competition_type_id = ct.id)
SET ac.option_value = 1 WHERE ct.name = 'Generic' AND ac.option_name = 1;
UPDATE assessor_count_option ac JOIN competition_type ct ON (ac.competition_type_id = ct.id)
SET ac.option_value = 3 WHERE ct.name = 'Generic' AND ac.option_name = 3;
UPDATE assessor_count_option ac JOIN competition_type ct ON (ac.competition_type_id = ct.id)
SET ac.option_value = 5 WHERE ct.name = 'Generic' AND ac.option_name = 5;

