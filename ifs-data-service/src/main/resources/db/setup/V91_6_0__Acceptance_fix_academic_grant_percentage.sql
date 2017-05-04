-- Update the value of the academic grant percentage for all ifs competitions
UPDATE competition
SET academic_grant_percentage=100
WHERE non_ifs=0;