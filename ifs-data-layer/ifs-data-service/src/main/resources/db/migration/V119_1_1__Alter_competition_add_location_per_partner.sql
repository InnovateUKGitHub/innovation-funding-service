--IFS-2920 - To decide for a competition, whether location per partner needs to be captured during project setup. Defaults to true.
ALTER TABLE competition ADD COLUMN location_per_partner BIT(1) DEFAULT TRUE;

--Set to false for all existing competitions so far (somewhere around Mar 2018). Will be set to true for some selected existing competitions.
UPDATE competition
SET location_per_partner = FALSE
WHERE 1=1;