-- IFS-12012 Update new hecp finance row type

ALTER TABLE finance_row MODIFY COLUMN type enum('LABOUR',
    'OVERHEADS',
    'HECP_INDIRECT_COSTS',
    'PROCUREMENT_OVERHEADS',
    'MATERIALS',
    'EQUIPMENT',
    'CAPITAL_USAGE',
    'OTHER_GOODS',
    'SUBCONTRACTING_COSTS',
    'TRAVEL',
    'OTHER_COSTS',
    'FINANCE',
    'OTHER_FUNDING',
    'YOUR_FINANCE',
    'VAT',
    'GRANT_CLAIM_AMOUNT',
    'ASSOCIATE_SALARY_COSTS',
    'ASSOCIATE_DEVELOPMENT_COSTS',
    'CONSUMABLES',
    'ASSOCIATE_SUPPORT',
    'KNOWLEDGE_BASE',
    'ESTATE_COSTS',
    'ADDITIONAL_COMPANY_COSTS',
    'KTP_TRAVEL',
    'PREVIOUS_FUNDING',
    'ACADEMIC_AND_SECRETARIAL_SUPPORT',
    'INDIRECT_COSTS'
);

ALTER TABLE competition_finance_row_types MODIFY COLUMN finance_row_type enum (
    'LABOUR',
    'OVERHEADS',
    'HECP_INDIRECT_COSTS',
    'PROCUREMENT_OVERHEADS',
    'MATERIALS',
    'EQUIPMENT',
    'CAPITAL_USAGE',
    'OTHER_GOODS',
    'SUBCONTRACTING_COSTS',
    'TRAVEL',
    'OTHER_COSTS',
    'FINANCE',
    'OTHER_FUNDING',
    'YOUR_FINANCE',
    'VAT',
    'GRANT_CLAIM_AMOUNT',
    'ASSOCIATE_SALARY_COSTS',
    'ASSOCIATE_DEVELOPMENT_COSTS',
    'CONSUMABLES',
    'ASSOCIATE_SUPPORT',
    'KNOWLEDGE_BASE',
    'ESTATE_COSTS',
    'ADDITIONAL_COMPANY_COSTS',
    'KTP_TRAVEL',
    'PREVIOUS_FUNDING',
    'ACADEMIC_AND_SECRETARIAL_SUPPORT',
    'INDIRECT_COSTS'
) NOT NULL;