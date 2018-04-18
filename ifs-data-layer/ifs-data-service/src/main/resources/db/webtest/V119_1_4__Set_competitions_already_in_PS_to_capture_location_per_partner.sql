--IFS-2920 - Set competitions already in Project Setup to capture location per partner
UPDATE competition SET location_per_partner = TRUE WHERE id = 14;