-- Needed to remove the knowledge base and address's as they are included in the mysqldump, cannot just exclude table as need the other address's for web test
SET foreign_key_checks = 0;
DELETE FROM knowledge_base;
DELETE FROM address;
SET foreign_key_checks = 1;