
-- Organisation sizes
SET @small=1;
SET @medium=2;
SET @large=3;

-- Competition types
SET @programme=1;
SET @sector=5;
SET @generic=6;

-- Organisation types
SET @RTO=3;
SET @charity=4;

-- Research categories
SET @feasibility = (SELECT id FROM category WHERE name='Feasibility studies');
SET @industrial = (SELECT id FROM category WHERE name='Industrial research');
SET @experimental = (SELECT id FROM category WHERE name='Experimental development');

-- Delete old items to avoid duplicates
DELETE FROM grant_claim_maximum
WHERE organisation_size_id IS NULL
AND organisation_type_id IN (@RTO, @charity) AND category_id IN (@feasibility, @industrial, @experimental);

-- Add a new row for each combination to have organisation size id 1 (small)
-- Feasibility
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @small, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @small, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @small, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @small, @charity, @programme, '100');

-- Industrial research
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @small, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @small, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @small, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @small, @charity, @programme, '100');

-- Experimental development
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @small, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @small, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @small, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @small, @charity, @programme, '100');


-- Add a new row for each combination to have organisation size id 2 (medium)
-- Feasibility
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES ( @feasibility, @medium, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @medium, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @medium, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @medium, @charity, @programme, '100');

-- Industrial research
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @medium, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @medium, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @medium, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @medium, @charity, @programme, '100');

-- Experimental development
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @medium, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @medium, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @medium, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @medium, @charity, @programme, '100');


-- Add a new row for each combination to have organisation size id 3 (large)
-- Feasibility
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @large, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @large, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @large, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@feasibility, @large, @charity, @programme, '100');

-- Industrial research
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @large, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @large, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @large, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@industrial, @large, @charity, @programme, '100');

-- Experimental development
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @large, @RTO, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @large, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @large, @RTO, @programme, '100');
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (@experimental, @large, @charity, @programme, '100');

-- Add the right values for the new competition type (same as programme)
INSERT INTO grant_claim_maximum (`category_id`, `organisation_size_id`, `organisation_type_id`,`competition_type_id`, `maximum`)
SELECT `category_id`, `organisation_size_id`, `organisation_type_id`, @generic, `maximum`
FROM grant_claim_maximum
WHERE competition_type_id = @programme;