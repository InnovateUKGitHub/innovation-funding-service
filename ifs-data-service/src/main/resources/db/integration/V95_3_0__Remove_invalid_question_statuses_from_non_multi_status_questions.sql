DELETE FROM question_status WHERE id IN (

  SELECT duplicate.id FROM (SELECT qs2.id FROM question q
    JOIN question_status qs ON qs.question_id = q.id
    JOIN question_status qs2 ON qs2.question_id = q.id
      AND qs2.application_id = qs.application_id
      AND qs2.id > qs.id
    WHERE q.multiple_statuses = 0
  ) AS duplicate

);