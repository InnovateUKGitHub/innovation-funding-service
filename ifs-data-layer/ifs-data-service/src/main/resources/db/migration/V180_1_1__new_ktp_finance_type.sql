-- new finance priorities
ALTER TABLE competition_finance_row_types ADD column priority INT(11);

UPDATE competition_finance_row_types set priority = 1 where finance_row_type = 'LABOUR';
UPDATE competition_finance_row_types set priority = 2 where finance_row_type = 'OVERHEADS';
UPDATE competition_finance_row_types set priority = 3 where finance_row_type = 'MATERIALS';
UPDATE competition_finance_row_types set priority = 4 where finance_row_type = 'CAPITAL_USAGE';
UPDATE competition_finance_row_types set priority = 5 where finance_row_type = 'SUBCONTRACTING_COSTS';
UPDATE competition_finance_row_types set priority = 6 where finance_row_type = 'TRAVEL';
UPDATE competition_finance_row_types set priority = 7 where finance_row_type = 'OTHER_COSTS';
UPDATE competition_finance_row_types set priority = 8 where finance_row_type = 'FINANCE';
UPDATE competition_finance_row_types set priority = 9 where finance_row_type = 'OTHER_FUNDING';
UPDATE competition_finance_row_types set priority = 10 where finance_row_type = 'YOUR_FINANCE';

UPDATE competition_finance_row_types set priority = 8 where finance_row_type = 'GRANT_CLAIM_AMOUNT';

UPDATE competition_finance_row_types set priority = 2 where finance_row_type = 'PROCUREMENT_OVERHEADS';
UPDATE competition_finance_row_types set priority = 11 where finance_row_type = 'VAT';

UPDATE competition_finance_row_types set priority = 99 where priority IS NULL;

ALTER TABLE competition_finance_row_types MODIFY priority INT(11) NOT NULL;
