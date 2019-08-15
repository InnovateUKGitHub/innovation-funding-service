-- Update new finance row type

ALTER TABLE finance_row MODIFY COLUMN type enum (
    'LABOUR',
    'OVERHEADS',
    'MATERIALS',
    'CAPITAL_USAGE',
    'SUBCONTRACTING_COSTS',
    'TRAVEL',
    'OTHER_COSTS',
    'FINANCE',
    'OTHER_FUNDING',
    'YOUR_FINANCE',
    'GRANT_CLAIM_AMOUNT'
);

ALTER TABLE competition_finance_row_types MODIFY COLUMN finance_row_type enum (
    'LABOUR',
    'OVERHEADS',
    'MATERIALS',
    'CAPITAL_USAGE',
    'SUBCONTRACTING_COSTS',
    'TRAVEL',
    'OTHER_COSTS',
    'FINANCE',
    'OTHER_FUNDING',
    'YOUR_FINANCE',
    'GRANT_CLAIM_AMOUNT'
) NOT NULL;