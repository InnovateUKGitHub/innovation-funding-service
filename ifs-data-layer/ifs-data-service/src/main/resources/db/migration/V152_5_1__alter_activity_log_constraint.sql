ALTER TABLE activity_log
DROP FOREIGN KEY fk_activity_log_thread_id;

ALTER TABLE activity_log
ADD CONSTRAINT fk_activity_log_thread_id
    FOREIGN KEY (thread_id) REFERENCES thread(id) ON DELETE SET NULL;