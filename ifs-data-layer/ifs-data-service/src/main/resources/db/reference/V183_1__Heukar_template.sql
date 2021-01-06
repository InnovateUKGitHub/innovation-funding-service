INSERT INTO competition_type (name, active) VALUES ('HEUKAR', 0);
set @competition_type_id = (SELECT LAST_INSERT_ID());

INSERT INTO assessor_count_option (competition_type_id, option_name, option_value, default_option) VALUES (@competition_type_id, '0', '1', 1);