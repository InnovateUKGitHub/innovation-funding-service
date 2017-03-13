-- The competition set up status is considered complete if the public content has been published.
-- This patch retrospectively adds a competition set up status entry for competitions which have published public content
-- and avoids duplicates.
INSERT INTO competition_setup_status (competition_id, `section`, status)
SELECT c.id, 'CONTENT', 1
  FROM competition AS c
  JOIN public_content AS pc ON c.id = pc.competition_id
 WHERE pc.publish_date IS NOT NULL
   AND NOT EXISTS (SELECT css.competition_id
                     FROM competition_setup_status AS css
                    WHERE css.competition_id = c.id
                      AND `section` = 'CONTENT')
