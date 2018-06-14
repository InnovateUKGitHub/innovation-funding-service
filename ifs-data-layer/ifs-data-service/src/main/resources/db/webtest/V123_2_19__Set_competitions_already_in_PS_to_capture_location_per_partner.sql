--IFS-2920 - Set competitions already in Project Setup to capture location per partner
UPDATE competition SET location_per_partner = FALSE;
UPDATE competition SET location_per_partner = TRUE WHERE name IN ('Internet of Things', 'New designs for a circular economy');