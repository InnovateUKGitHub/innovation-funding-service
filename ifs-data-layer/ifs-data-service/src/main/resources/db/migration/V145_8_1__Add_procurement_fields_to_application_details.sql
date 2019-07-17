-- IFS-6074: Addition of three questions in application details.

ALTER TABLE application ADD COLUMN competition_referral_source VARCHAR(255);
ALTER TABLE application ADD COLUMN company_age VARCHAR(255);
ALTER TABLE application ADD COLUMN company_primary_focus VARCHAR(255);