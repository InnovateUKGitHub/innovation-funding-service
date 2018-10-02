-- IFS-4459 - Project Documents - Create default documents for all competitions which have not yet reached the Project Setup phase.
INSERT INTO document_config
(competition_id, title, guidance, editable, enabled, type)
SELECT c.id AS competition_id,
'Collaboration agreement' AS title,
'Enter guidance for Collaboration agreement' AS guidance,
false AS editable,
true AS enabled,
'ProjectDocument' AS type FROM competition c
WHERE c.id NOT IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	WHERE EXISTS (SELECT a.manage_funding_email_date FROM application a WHERE a.competition = c.id AND a.funding_decision = 'FUNDED' AND a.manage_funding_email_date IS NOT NULL)
	AND c.setup_complete = TRUE AND c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
	);

INSERT INTO document_config
(competition_id, title, guidance, editable, enabled, type)
SELECT c.id AS competition_id,
'Exploitation plan' AS title,
'Enter guidance for Exploitation plan' AS guidance,
false AS editable,
true AS enabled,
'ProjectDocument' AS type FROM competition c
WHERE c.id NOT IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	WHERE EXISTS (SELECT a.manage_funding_email_date FROM application a WHERE a.competition = c.id AND a.funding_decision = 'FUNDED' AND a.manage_funding_email_date IS NOT NULL)
	AND c.setup_complete = TRUE AND c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
);

