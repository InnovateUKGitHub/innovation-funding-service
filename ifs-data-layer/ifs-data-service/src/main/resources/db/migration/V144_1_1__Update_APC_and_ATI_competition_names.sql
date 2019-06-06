-- IFS-5867 Update names of two competition types

UPDATE competition_type SET name = 'BEIS: Advanced Propulsion Centre (APC) Programme' WHERE name = 'Advanced Propulsion Centre';
UPDATE competition_type SET name = 'BEIS: Aerospace Technology Institute (ATI) Programme' WHERE name = 'Aerospace Technology Institute';