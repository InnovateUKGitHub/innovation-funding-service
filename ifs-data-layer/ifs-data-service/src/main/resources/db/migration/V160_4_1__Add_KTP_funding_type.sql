-- IFS-7146 add KTP funding type
ALTER TABLE competition MODIFY COLUMN funding_type enum('GRANT','LOAN','PROCUREMENT', 'KTP');
