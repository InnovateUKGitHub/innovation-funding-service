-- IFS-2833 Alter question table, add question_setup_type.
ALTER TABLE question ADD COLUMN question_setup_type VARCHAR(255);
