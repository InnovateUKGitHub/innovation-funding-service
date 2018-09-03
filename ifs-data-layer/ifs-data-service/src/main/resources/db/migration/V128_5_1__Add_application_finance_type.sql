-- IFS-3284 Begin migration of competition.full_application_finances to new column competition.application_finance_type
ALTER TABLE competition ADD application_finance_type ENUM('STANDARD', 'STANDARD_WITH_VAT', 'NO_FINANCES') NULL;

-- Migrate data where full_application_finance was set, but not for templates
UPDATE competition SET application_finance_type = 'STANDARD' WHERE full_application_finance = 1 AND template = 0;
UPDATE competition SET application_finance_type = 'NO_FINANCES' WHERE full_application_finance = 0 AND template = 0;

-- Migrate data where full_application_finance was not set, for both Competitions and templates
UPDATE competition SET application_finance_type = 'NO_FINANCES' WHERE full_application_finance IS NULL;