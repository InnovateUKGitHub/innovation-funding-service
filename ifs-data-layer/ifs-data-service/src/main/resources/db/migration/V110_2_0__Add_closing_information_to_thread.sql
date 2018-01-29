/*
- Purpose of this patch is to alter the entity 'thread' and add two extra fields in it.
- As per ticket IFS-2638, the queries' threads under Finance checks in PS, can now be marked as resolved.
- Thus, we save the user that resolves the thread and the date.
*/
ALTER TABLE thread ADD COLUMN closed_by_user_id BIGINT(20) NULL;

ALTER TABLE thread ADD CONSTRAINT closed_by_user_id_fk FOREIGN KEY (closed_by_user_id)
  REFERENCES `user` (id);

ALTER TABLE thread ADD COLUMN closed_date TIMESTAMP NULL;