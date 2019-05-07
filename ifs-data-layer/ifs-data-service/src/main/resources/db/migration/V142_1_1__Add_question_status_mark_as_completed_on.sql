-- IFS-5676 add new timestamp for mark as complete
ALTER TABLE question_status ADD COLUMN marked_as_complete_on DATETIME;