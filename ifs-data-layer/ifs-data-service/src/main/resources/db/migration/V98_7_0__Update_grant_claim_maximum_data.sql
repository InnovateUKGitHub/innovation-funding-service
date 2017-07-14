
-- Organisation sizes
SET @small=1;
SET @medium=2;
SET @large=3;

-- Competition types
SET @programme=1;
SET @sector=5;

-- Organisation types
SET @RTO=3;
SET @charity=4;

-- Research categories
SELECT @feasibility := id FROM category WHERE name='Feasibility studies';
SELECT @industrial  := id FROM category WHERE name='Industrial research';
SELECT @experimental:= id FROM category WHERE name='Experimental development';

-- Update the first existing rows to have organisation size id 1 (small)
UPDATE grant_claim_maximum SET organisation_size_id=@small
WHERE id IN (
SELECT id FROM (SELECT * from grant_claim_maximum) AS gcm
    WHERE organisation_type_id IN (@RTO, @charity) AND competition_type_id IN (@programme, @sector) AND category_id IN (@feasibility, @industrial, @experimental)
    GROUP BY category_id
);

-- Update the duplicate existing rows to have organisation size id 2 (medium)
UPDATE grant_claim_maximum SET organisation_size_id=@medium
WHERE id IN (
SELECT id FROM (SELECT * from grant_claim_maximum) AS gcm
    WHERE organisation_type_id IN (@RTO, @charity) AND competition_type_id IN (@programme, @sector) AND category_id IN (@feasibility, @industrial, @experimental)
    AND organisation_size_id IS NULL
);

-- Add a new row for each combination to have organisation size id 3 (large)
-- Feasibility
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @feasibility, @large, @rto, @sector, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @feasibility, @large, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @feasibility, @large, @rto, @programme, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @feasibility, @large, @charity, @programme, '100');

-- Industrial research
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @industrial, @large, @rto, @sector, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @industrial, @large, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @industrial, @large, @rto, @programme, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @industrial, @large, @charity, @programme, '100');

-- Experimental development
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @experimental, @large, @rto, @sector, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @experimental, @large, @charity, @sector, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @experimental, @large, @rto, @programme, '100');
INSERT INTO grant_claim_maximum (`id`, `category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`)
    VALUES (NULL, @experimental, @large, @charity, @programme, '100');
