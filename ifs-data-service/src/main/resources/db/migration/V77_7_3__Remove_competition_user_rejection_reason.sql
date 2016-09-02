-- remove the old competition_user_rejection_reason table in favour of rejection_reason
ALTER TABLE competition_user
  DROP FOREIGN KEY competition_user_to_competition_user_rejection_reason;

DROP TABLE competition_user_rejection_reason;

ALTER TABLE competition_user ADD CONSTRAINT competition_user_to_rejection_reason_fk
  FOREIGN KEY (rejection_reason_id) REFERENCES rejection_reason(id);
