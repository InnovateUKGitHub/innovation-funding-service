INSERT INTO competition_type (name, active) VALUES ('Ofgem', 1);
SET @competition_type_id = (SELECT LAST_INSERT_ID());