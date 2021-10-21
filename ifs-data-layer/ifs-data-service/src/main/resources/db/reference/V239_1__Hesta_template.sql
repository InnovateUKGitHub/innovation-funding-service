INSERT INTO competition_type (name, active, template_competition_id) VALUES ('Hesta', 0, @template_id);
SET @competition_type_id = (SELECT LAST_INSERT_ID());