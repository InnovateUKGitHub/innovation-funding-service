SET @feasibility_studies=33;
SET @industrial_research=34;
SET @experimental_development=35;

SET @business=1;
SET @public_sector=3;
SET @charity=4;
SET @university=5;
SET @rto=6;
SET @catapult=7;
SET @public_sector_research=8;
SET @research_council=9;

SET @small=1;
SET @medium=2;
SET @large=3;

SET @programme=1;
SET @sector=5;

INSERT INTO `grant_claim_maximum` (`category_id`, `organisation_size_id`, `organisation_type_id`, `competition_type_id`, `maximum`) VALUES
    -- Programme competition values.
    -- Business
    (@feasibility_studies, @small, @business, @programme, 70),
    (@feasibility_studies, @medium, @business, @programme, 60),
    (@feasibility_studies, @large, @business, @programme, 50),

    (@industrial_research, @small, @business, @programme, 70),
    (@industrial_research, @medium, @business, @programme, 60),
    (@industrial_research, @large, @business, @programme, 50),

    (@experimental_development, @small, @business, @programme, 45),
    (@experimental_development, @medium, @business, @programme, 35),
    (@experimental_development, @large, @business, @programme, 25),

    -- Public sector
    (@feasibility_studies, NULL, @public_sector, @programme, 100),
    (@industrial_research, NULL, @public_sector, @programme, 100),
    (@experimental_development, NULL, @public_sector, @programme, 100),

    -- Charity
    (@feasibility_studies, NULL, @charity, @programme, 100),
    (@industrial_research, NULL, @charity, @programme, 100),
    (@experimental_development, NULL, @charity, @programme, 100),

    -- University
    (@feasibility_studies, NULL, @university, @programme, 100),
    (@industrial_research, NULL, @university, @programme, 100),
    (@experimental_development, NULL, @university, @programme, 100),

    -- RTO
    (@feasibility_studies, NULL, @rto, @programme, 100),
    (@industrial_research, NULL, @rto, @programme, 100),
    (@experimental_development, NULL, @rto, @programme, 100),

    -- Catapult
    (@feasibility_studies, NULL, @catapult, @programme, 100),
    (@industrial_research, NULL, @catapult, @programme, 100),
    (@experimental_development, NULL, @catapult, @programme, 100),

    -- Public sector research
    (@feasibility_studies, NULL, @public_sector_research, @programme, 100),
    (@industrial_research, NULL, @public_sector_research, @programme, 100),
    (@experimental_development, NULL, @public_sector_research, @programme, 100),

    -- Research council
    (@feasibility_studies, NULL, @research_council, @programme, 100),
    (@industrial_research, NULL, @research_council, @programme, 100),
    (@experimental_development, NULL, @research_council, @programme, 100),
    -- Sector competition values.
    -- Business
    (@feasibility_studies, @small, @business, @sector, 70),
    (@feasibility_studies, @medium, @business, @sector, 60),
    (@feasibility_studies, @large, @business, @sector, 50),

    (@industrial_research, @small, @business, @sector, 70),
    (@industrial_research, @medium, @business, @sector, 60),
    (@industrial_research, @large, @business, @sector, 50),

    (@experimental_development, @small, @business, @sector, 45),
    (@experimental_development, @medium, @business, @sector, 35),
    (@experimental_development, @large, @business, @sector, 25),

    -- Public sector
    (@feasibility_studies, NULL, @public_sector, @sector, 100),
    (@industrial_research, NULL, @public_sector, @sector, 100),
    (@experimental_development, NULL, @public_sector, @sector, 100),

    -- Charity
    (@feasibility_studies, NULL, @charity, @sector, 100),
    (@industrial_research, NULL, @charity, @sector, 100),
    (@experimental_development, NULL, @charity, @sector, 100),

    -- University
    (@feasibility_studies, NULL, @university, @sector, 100),
    (@industrial_research, NULL, @university, @sector, 100),
    (@experimental_development, NULL, @university, @sector, 100),

    -- RTO
    (@feasibility_studies, NULL, @rto, @sector, 100),
    (@industrial_research, NULL, @rto, @sector, 100),
    (@experimental_development, NULL, @rto, @sector, 100),

    -- Catapult
    (@feasibility_studies, NULL, @catapult, @sector, 100),
    (@industrial_research, NULL, @catapult, @sector, 100),
    (@experimental_development, NULL, @catapult, @sector, 100),

    -- Public sector research
    (@feasibility_studies, NULL, @public_sector_research, @sector, 100),
    (@industrial_research, NULL, @public_sector_research, @sector, 100),
    (@experimental_development, NULL, @public_sector_research, @sector, 100),

    -- Research council
    (@feasibility_studies, NULL, @research_council, @sector, 100),
    (@industrial_research, NULL, @research_council, @sector, 100),
    (@experimental_development, NULL, @research_council, @sector, 100);