-- Needed to remove the knowledge base and address's as they are included in the mysqldump, cannot just exclude table as need the other address's for web test
SET SQL_SAFE_UPDATES = 0;
delete from knowledge_base;
DELETE FROM address;
SET SQL_SAFE_UPDATES = 1;