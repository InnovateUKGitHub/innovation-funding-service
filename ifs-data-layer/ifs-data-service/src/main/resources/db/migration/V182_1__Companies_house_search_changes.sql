ALTER TABLE organisation ADD COLUMN date_of_incorporation DATETIME DEFAULT NULL;
ALTER TABLE organisation ADD COLUMN business_type varchar(255) DEFAULT NULL;
ALTER TABLE organisation ADD COLUMN organisation_number varchar(255) DEFAULT NULL;
ALTER TABLE organisation ADD COLUMN sic_code_id bigint(20);
ALTER TABLE organisation ADD COLUMN executive_officer_id bigint(20);

CREATE TABLE sic_code (
                     	id bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
                     	organisation_id bigint(20) NOT NULL,
                     	sic_code VARCHAR(255)
);

CREATE TABLE executive_officer (
                     	id bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
                     	organisation_id bigint(20) NOT NULL,
                     	name VARCHAR(255)
);
ALTER TABLE organisation ADD CONSTRAINT `FK_sic_code_id` FOREIGN KEY (`sic_code_id`) REFERENCES `sic_code` (`id`);
ALTER TABLE organisation ADD CONSTRAINT `FK_executive_officer_id` FOREIGN KEY (`executive_officer_id`) REFERENCES `executive_officer` (`id`);



