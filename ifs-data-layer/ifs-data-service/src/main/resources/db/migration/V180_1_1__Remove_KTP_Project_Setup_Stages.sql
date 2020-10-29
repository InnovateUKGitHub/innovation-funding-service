-- IFS-8329 remove project setup stages from in flight KTP competitions

DELETE ps
FROM project_stages ps
INNER JOIN competition c on c.id = ps.competition_id
WHERE c.funding_type = "KTP"
AND ps.project_setup_stage in ("DOCUMENTS", "BANK_DETAILS", "SPEND_PROfILE");