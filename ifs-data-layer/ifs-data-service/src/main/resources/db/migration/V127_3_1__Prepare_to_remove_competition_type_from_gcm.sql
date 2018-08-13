-- IFS-3954 ZDD cleanup for IFS-3818
-- A new reference to this column was reintroduced in the codebase by IFS-3622.
-- We will remove the reference and stop the column from being used so it can be removed by a later ZDD task.
-- To allow new rows to be inserted the column will be made nullable.
ALTER TABLE grant_claim_maximum DROP FOREIGN KEY grant_claim_maximum_competition_type_fk;
ALTER TABLE grant_claim_maximum MODIFY grant_claim_maximum BIGINT(20) NULL;