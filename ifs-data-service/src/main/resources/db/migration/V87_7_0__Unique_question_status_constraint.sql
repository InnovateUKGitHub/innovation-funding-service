set session old_alter_table=1;
ALTER IGNORE TABLE `question_status` ADD UNIQUE KEY question_status_complete_unique(application_id, marked_as_complete_by_id, question_id);
set session old_alter_table=0;