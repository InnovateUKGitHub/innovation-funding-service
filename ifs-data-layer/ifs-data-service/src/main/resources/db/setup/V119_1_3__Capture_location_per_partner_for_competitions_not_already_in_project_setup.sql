--IFS-2920 - Capture location per partner for all competitions which have not yet reached the project setup stage
UPDATE competition c
SET c.location_per_partner = TRUE
WHERE NOT EXISTS (SELECT a.manage_funding_email_date FROM application a WHERE a.competition = c.id AND a.funding_decision = 'FUNDED' AND a.manage_funding_email_date IS NOT NULL)
AND c.template = FALSE AND c.non_ifs = FALSE