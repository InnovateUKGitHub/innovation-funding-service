INSERT INTO competition_type (name, active) VALUES ('Ofgem', 1);
SET @competition_type_id = (SELECT LAST_INSERT_ID());

INSERT INTO assessor_count_option (competition_type_id, option_name, option_value, default_option) VALUES (@competition_type_id, '1', '1', 0);
INSERT INTO assessor_count_option (competition_type_id, option_name, option_value, default_option) VALUES (@competition_type_id, '3', '3', 0);
INSERT INTO assessor_count_option (competition_type_id, option_name, option_value, default_option) VALUES (@competition_type_id, '5', '5', 1);