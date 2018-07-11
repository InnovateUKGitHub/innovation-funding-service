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