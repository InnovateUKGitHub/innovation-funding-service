-- IFS-8453 - include All finance view for assessor

ALTER TABLE competition
    MODIFY COLUMN assessor_finance_view enum('OVERVIEW', 'DETAILED', 'ALL');

ALTER TABLE competition_assessment_config
    MODIFY COLUMN assessor_finance_view enum('OVERVIEW', 'DETAILED', 'ALL');