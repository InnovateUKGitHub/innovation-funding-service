-- IFS-4631 Migrate live projects to new project docs functionality

INSERT INTO document_config
(competition_id, title, guidance, editable, enabled, type)
SELECT c.id AS competition_id,
'Collaboration agreement' AS title,
'Enter guidance for Collaboration agreement' AS guidance,
false AS editable,
true AS enabled,
'ProjectDocument' AS type FROM competition c
WHERE c.id IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	LEFT JOIN document_config dc ON (c.id = dc.competition_id)
	WHERE EXISTS (SELECT a.manage_funding_email_date FROM application a WHERE a.competition = c.id AND a.funding_decision = 'FUNDED' AND a.manage_funding_email_date IS NOT NULL)
	AND c.setup_complete = TRUE AND c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
	AND ct.name != 'The Prince''s Trust'
	AND dc.competition_id IS NULL
);

INSERT INTO document_config
(competition_id, title, guidance, editable, enabled, type)
SELECT c.id AS competition_id,
'Exploitation plan' AS title,
'Enter guidance for Exploitation plan' AS guidance,
false AS editable,
true AS enabled,
'ProjectDocument' AS type FROM competition c
WHERE c.id IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	LEFT JOIN document_config dc ON c.id = dc.competition_id
	WHERE EXISTS (SELECT a.manage_funding_email_date FROM application a WHERE a.competition = c.id AND a.funding_decision = 'FUNDED' AND a.manage_funding_email_date IS NOT NULL)
	AND c.setup_complete = TRUE AND c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
	AND ct.name != 'The Prince''s Trust'
	AND dc.title = 'Collaboration agreement'
	AND NOT EXISTS (SELECT * FROM document_config dc WHERE dc.title != 'Collaboration agreement' AND dc.competition_id = c.id)
);

INSERT INTO document_config_file_type
(document_config_id, file_type_id)
SELECT dc.id AS document_config_id,
(SELECT id FROM file_type WHERE name = 'PDF') AS file_type_id FROM document_config dc
WHERE dc.id IN (
  SELECT dc.id FROM document_config dc
  LEFT JOIN document_config_file_type dcft ON (dc.id = dcft.document_config_id)
  WHERE dcft.document_config_id IS NULL
);