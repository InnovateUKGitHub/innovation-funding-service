-- IFS 6090

-- Add competition finance row join table
CREATE TABLE competition_finance_row_types (
  competition_id bigint(20) NOT NULL,
  finance_row_type enum (
    'LABOUR',
    'OVERHEADS',
    'PROCUREMENT_OVERHEADS',
    'MATERIALS',
    'CAPITAL_USAGE',
    'SUBCONTRACTING_COSTS',
    'TRAVEL',
    'OTHER_COSTS',
    'FINANCE',
    'OTHER_FUNDING',
    'YOUR_FINANCE',
    'VAT'
  ) NOT NULL,
  PRIMARY KEY (competition_id,finance_row_type),
  CONSTRAINT fk_competition_finance_row_types_competition_id FOREIGN KEY (competition_id) REFERENCES competition(id)
);

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'LABOUR' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'OVERHEADS' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'MATERIALS' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'CAPITAL_USAGE' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'SUBCONTRACTING_COSTS' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'TRAVEL' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'OTHER_COSTS' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'FINANCE' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'OTHER_FUNDING' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'YOUR_FINANCE' as finance_row_type
FROM competition;

INSERT INTO competition_finance_row_types (competition_id, finance_row_type)
SELECT id as competition_id, 'VAT' as finance_row_type
FROM competition;