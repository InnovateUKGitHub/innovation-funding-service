UPDATE competition SET covid_type='DE_MINIMIS' WHERE name = '583 Covid deminis round 1' or name = '583 Covid deminis round 1 project setup';
UPDATE competition SET covid_type='ADDITIONAL_FUNDING' WHERE name = '596 Covid grants framework group';
UPDATE competition SET covid_type='DE_MINIMIS_ROUND_2' WHERE name = '599 Covid de minimis round 2';

UPDATE competition SET use_docusign_for_grant_offer_letter=1 WHERE covid_type IS NOT NULL;
UPDATE project p inner join application a on a.id = p.application_id INNER JOIN competition c on c.id = a.competition SET p.use_docusign_for_grant_offer_letter=1 WHERE c.covid_type is not null;

-- ADDITIONAL_FUNDING
SET @additional_funding_id = (SELECT id FROM competition WHERE covid_type = 'ADDITIONAL_FUNDING');
UPDATE competition SET has_assessment_stage=false WHERE id = @additional_funding_id;
DELETE FROM competition_finance_row_types WHERE competition_id = @additional_funding_id and finance_row_type='FINANCE';
INSERT INTO competition_finance_row_types(competition_id, finance_row_type) VALUES (@additional_funding_id, 'GRANT_CLAIM_AMOUNT');


-- DE_MINIMIS_ROUND_2
SET @de_minimis_round_2 = (SELECT id FROM competition WHERE covid_type = 'DE_MINIMIS_ROUND_2');
DELETE FROM competition_finance_row_types WHERE competition_id = @de_minimis_round_2 and finance_row_type='FINANCE';
INSERT INTO competition_finance_row_types(competition_id, finance_row_type) VALUES (@de_minimis_round_2, 'GRANT_CLAIM_AMOUNT');


-- TODO
-- docusign

-- These following queries will not be needed on prod and will be setup by comp admins.
UPDATE competition SET covid_type='DE_MINIMIS' WHERE name = 'Project Setup Comp 18';

-- All comps are 100% funding
SET @current_max_grant_id = (select max(id) from grant_claim_maximum);

INSERT INTO grant_claim_maximum (organisation_size_id, category_id, maximum)
SELECT s.id   AS organisation_size_id,
       c.id   AS category_id,
       100    AS maximum
FROM category c
INNER JOIN organisation_size s
WHERE c.type='RESEARCH_CATEGORY';

DELETE g FROM grant_claim_maximum_competition g
INNER JOIN competition c
    ON c.id = g.competition_id
WHERE c.covid_type IS NOT NULL;

INSERT INTO grant_claim_maximum_competition (grant_claim_maximum_id, competition_id)
SELECT m.id as grant_claim_maximum_id,
       c.id as competition_id
FROM competition c
INNER JOIN grant_claim_maximum m
WHERE m.id > @current_max_grant_id
AND c.covid_type IS NOT NULL;

-- 25 question score, no feedback for covid round 2.
update form_input set active = 0 where competition_id = @de_minimis_round_2 and form_input_type_id=2 and scope='ASSESSMENT';
update question set assessor_maximum_score = 25 where competition_id = @de_minimis_round_2;