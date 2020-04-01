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

