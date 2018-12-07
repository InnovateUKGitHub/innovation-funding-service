-- IFS-4459 - Project Documents - Create default documents for all competitions which have not yet reached the Project Setup phase.
-- Expression of interest and Prince's trust competitions will never reach PS so are excluded by default
INSERT INTO document_config
(competition_id, title, guidance, editable, enabled, type)
SELECT c.id AS competition_id,
'Collaboration agreement' AS title,
'<p>The collaboration agreement covers how the consortium will work together on the project and exploit its results. It must be signed by all partners.</p>

 <p>Please allow enough time to complete this document before your project start date.</p>

 <p>Guidance on completing a collaboration agreement can be found on the <a target="_blank" href="http://www.ipo.gov.uk/lambert">Lambert Agreement website</a>.</p>

 <p>Your collaboration agreement must be:</p>
 <ul class="list-bullet"><li>in portable document format (PDF)</li>
 <li>legible at 100% magnification</li>
 <li>less than 10MB in file size</li></ul>' AS guidance,
false AS editable,
true AS enabled,
'CompetitionDocument' AS type FROM competition c
WHERE c.id NOT IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	WHERE EXISTS (SELECT a.manage_funding_email_date FROM application a WHERE a.competition = c.id AND a.funding_decision = 'FUNDED' AND a.manage_funding_email_date IS NOT NULL)
	AND c.setup_complete = TRUE AND c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
	AND ct.name != 'The Prince''s Trust'
	);

INSERT INTO document_config
(competition_id, title, guidance, editable, enabled, type)
SELECT c.id AS competition_id,
'Exploitation plan' AS title,
'<p>This is a confirmation of your overall plan, setting out the business case for your project. This plan will change during the lifetime of the project.</p>

 <p>It should also describe partner activities that will exploit the results of the project so that:</p>
 <ul class="list-bullet"><li>changes in the commercial environment can be monitored and accounted for</li>
 <li>adequate resources are committed to exploitation</li>
 <li>exploitation can be monitored by the stakeholders</li></ul>

 <p>You can download an <a href="/files/exploitation_plan.doc" class="govuk-link">exploitation plan template</a>.</p>

 <p>The uploaded exploitation plan must be:</p>
 <ul class="list-bullet"><li>in portable document format (PDF)</li>
 <li>legible at 100% magnification</li>
 <li>less than 10MB in file size</li></ul>' AS guidance,
false AS editable,
true AS enabled,
'CompetitionDocument' AS type FROM competition c
WHERE c.id NOT IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	WHERE EXISTS (SELECT a.manage_funding_email_date FROM application a WHERE a.competition = c.id AND a.funding_decision = 'FUNDED' AND a.manage_funding_email_date IS NOT NULL)
	AND c.setup_complete = TRUE AND c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
	AND ct.name != 'The Prince''s Trust'
);

INSERT INTO document_config_file_type
(document_config_id, file_type_id)
SELECT id AS document_config_id,
(SELECT id FROM file_type WHERE name = 'PDF') AS file_type_id
FROM document_config;



