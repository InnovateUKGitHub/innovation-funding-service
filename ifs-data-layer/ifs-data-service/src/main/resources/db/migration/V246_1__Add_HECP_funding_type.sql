-- IFS-11269 - Add HECP funding type
ALTER TABLE competition MODIFY COLUMN funding_type enum('GRANT','LOAN','PROCUREMENT', 'KTP', 'HECP');