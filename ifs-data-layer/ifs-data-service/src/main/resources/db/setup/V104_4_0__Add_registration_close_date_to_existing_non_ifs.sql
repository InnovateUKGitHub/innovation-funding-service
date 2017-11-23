INSERT IGNORE INTO milestone(date, type, competition_id)
SELECT DATE_SUB(m.`date`, INTERVAL 7 DAY), 'REGISTRATION_DATE', c.id
FROM `milestone` m
JOIN `competition` c ON m.competition_id = c.id
WHERE c.non_ifs = 1
AND m.type = 'SUBMISSION_DATE';