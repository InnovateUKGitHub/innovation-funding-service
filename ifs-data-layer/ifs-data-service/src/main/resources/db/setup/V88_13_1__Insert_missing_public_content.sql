INSERT INTO `public_content` (competition_id)
SELECT `id` FROM `competition` c
WHERE NOT EXISTS ( SELECT * FROM `public_content` WHERE competition_id=c.id)
AND c.template=0;