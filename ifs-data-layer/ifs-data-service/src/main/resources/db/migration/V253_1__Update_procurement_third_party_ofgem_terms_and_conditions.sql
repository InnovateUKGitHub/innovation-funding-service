-- IFS-11567 - Third Party (Ofgem) T & C Changes
SET @existing_third_party_terms_id = 55;

UPDATE terms_and_conditions SET name = 'Third Party' WHERE id = @existing_third_party_terms_id;
