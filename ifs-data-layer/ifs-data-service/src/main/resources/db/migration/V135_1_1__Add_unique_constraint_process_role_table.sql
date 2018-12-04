-- 1. Create temporary table for the duplicate ids which will be deleted
CREATE TEMPORARY TABLE process_roles_to_delete (
    id bigint(20)
);

-- 2. Insert into temporary table the duplicate process roles excluding the highest id of each group of duplicates
INSERT INTO process_roles_to_delete
	SELECT id FROM process_role i WHERE EXISTS (
    SELECT id FROM process_role o
    WHERE o.application_id = i.application_id
    AND o.user_id = i.user_id
    AND o.role_id = i.role_id
    AND o.organisation_id = i.organisation_id
    GROUP BY application_id, organisation_id, role_id, user_id
    HAVING COUNT(*) > 1) AND id NOT IN (
    SELECT
        MAX(id)
    FROM
        process_role
    GROUP BY
        application_id, organisation_id, role_id, user_id
    HAVING
        COUNT(*) > 1
    )
;

-- 3. Delete duplicates
DELETE FROM process_role WHERE id IN (SELECT * FROM process_roles_to_delete);

-- 4. Drop temporary table
DROP TABLE process_roles_to_delete;

-- 5. Add unique constraint to process_role rows
ALTER TABLE process_role
ADD CONSTRAINT `UC_process_role_unique_row` UNIQUE (`application_id`, `role_id`, `user_Id`, `organisation_id`);