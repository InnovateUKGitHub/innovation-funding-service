-- IFS-3814 Fixing priorities of 'Other funding' question broken by IFS-3088.
-- 'Other funding' was incorrectly appearing ABOVE the 'Funding level' question on the Your Funding page for applicants

-- Bump the priority of all 'Other funding' questions on competitions that have the new view.
-- Now they will have a lower priority than 'Funding level' in all cases and appear at the bottom of the page again.
UPDATE question SET priority = priority + 2
    WHERE short_name="Other funding"
    AND competition_id IN (
        SELECT competition_id FROM (select competition_id, short_name FROM question) AS question_table
            WHERE short_name="Application team"
    )
;