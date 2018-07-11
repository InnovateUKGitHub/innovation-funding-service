-- IFS-3914 - correcting form_inputs with clashing priority levels.  This script will run an update to form_input priorities
-- with clashing levels to increment the priority of the higher-id form input and to run multiple times in case that
-- increment then causes it to clash with an already-higher priority form input (e.g. if 2 form inputs for the same question
-- and scope had priority level 0, the one with thre higher id would be incremented to 1.  This may however then cause it to clash
-- with another form input of priority 1 on the same question and scope, and so running this update again will move this
-- already-priority 1 form input up to 2, and so on and so on until no further clashes remain

DELIMITER //

CREATE PROCEDURE deduplicate_priorities()
BEGIN

  SET @duplicates_exist = 1;

  REPEAT

UPDATE form_input SET priority = priority + 1 where id in (
SELECT f.id FROM
(SELECT fi2.id
FROM form_input fi
JOIN form_input fi2 ON fi.id < fi2.id AND fi2.question_id = fi.question_id AND fi.scope = fi2.scope
WHERE fi.priority = fi2.priority
) as f
);


    SELECT @duplicates_exist := exists (SELECT 1
      FROM form_input fi
      JOIN form_input fi2 ON fi.id < fi2.id AND fi2.question_id = fi.question_id AND fi.scope = fi2.scope
      WHERE fi.priority = fi2.priority);

  UNTIL @duplicates_exist = 0 END REPEAT;

END //

CALL deduplicate_priorities() //

DROP PROCEDURE deduplicate_priorities //