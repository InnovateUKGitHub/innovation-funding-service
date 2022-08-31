-- IFS-12745-add-ktp-akt-funding-type
ALTER TABLE competition MODIFY COLUMN funding_type enum('GRANT','LOAN','PROCUREMENT','KTP','INVESTOR_PARTNERSHIPS','HECP', 'THIRDPARTY', 'KTP_AKT');