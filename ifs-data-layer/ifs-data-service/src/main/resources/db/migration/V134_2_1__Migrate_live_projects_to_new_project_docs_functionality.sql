-- IFS-4631 Migrate all competitions to new configurable documents excluding Expression of interest

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
WHERE c.id IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	LEFT JOIN document_config dc ON (c.id = dc.competition_id)
	WHERE c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
	AND dc.competition_id IS NULL
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
WHERE c.id IN (
	SELECT c.id FROM competition c
	LEFT JOIN competition_type ct ON (c.competition_type_id = ct.id)
	LEFT JOIN document_config dc ON c.id = dc.competition_id
	WHERE c.template = FALSE AND c.non_ifs = FALSE
	AND ct.name != 'Expression of interest'
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

-- Copy Collaboration file entry ids to project_document
-- Status comments are a new feature to be implemented therefore the default message will be Rejected

INSERT INTO project_document
(project_id, document_config_id, file_entry_id, status, status_comments)
SELECT p.id AS project_id,
dc.id AS document_config_id,
p.collaboration_agreement_file_entry_id AS file_entry_id,
'UPLOADED'  AS status,
'' AS status_comments
FROM project p
INNER JOIN application a ON p.application_id = a.id
INNER JOIN document_config dc ON a.competition = dc.competition_id
WHERE dc.title = 'Collaboration agreement'
AND p.collaboration_agreement_file_entry_id IS NOT NULL;

-- Copy Exploitation file entry ids to project_document
-- Status comments are a new feature to be implemented therefore the default message will be Rejected

INSERT INTO project_document
(project_id, document_config_id, file_entry_id, status, status_comments)
SELECT p.id AS project_id,
dc.id AS document_config_id,
p.exploitation_plan_file_entry_id AS file_entry_id,
'UPLOADED' AS status,
'' AS status_comments
FROM project p
INNER JOIN application a ON p.application_id = a.id
INNER JOIN document_config dc ON a.competition = dc.competition_id
WHERE dc.title = 'Exploitation plan'
AND p.exploitation_plan_file_entry_id IS NOT NULL;

-- If collaboration agreement and exploitation plan have been submitted then update project document status

UPDATE project_document
SET project_document.status = 'SUBMITTED'
WHERE project_document.project_id IN (SELECT id FROM project WHERE project.documents_submitted_date IS NOT NULL);

-- If collaboration agreement and exploitation plan have been rejected then update project document status

UPDATE project_document
SET project_document.status = 'REJECTED'
WHERE project_document.project_id IN (SELECT id FROM project WHERE project.other_documents_approved = 'REJECTED');

-- If collaboration agreement and exploitation plan have been accepted then update project document status

UPDATE project_document
SET project_document.status = 'APPROVED'
WHERE project_document.project_id IN (SELECT id FROM project WHERE project.other_documents_approved = 'APPROVED');

-- Remove collaboration agreement fk constraint

ALTER TABLE project
  DROP FOREIGN KEY project_ibfk_1;

-- Remove exploitation plan fk constraint

ALTER TABLE project
  DROP FOREIGN KEY project_ibfk_2;

--  Drop old unused columns

ALTER TABLE project DROP COLUMN collaboration_agreement_file_entry_id;
ALTER TABLE project DROP COLUMN exploitation_plan_file_entry_id;