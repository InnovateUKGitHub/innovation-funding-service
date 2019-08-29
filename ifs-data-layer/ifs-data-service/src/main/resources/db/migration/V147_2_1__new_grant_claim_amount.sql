-- IFS-6207 Update new finance row type

ALTER TABLE finance_row MODIFY COLUMN type enum (
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
    'VAT',
    'GRANT_CLAIM_AMOUNT'
);

ALTER TABLE competition_finance_row_types MODIFY COLUMN finance_row_type enum (
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
    'VAT',
    'GRANT_CLAIM_AMOUNT'
) NOT NULL;