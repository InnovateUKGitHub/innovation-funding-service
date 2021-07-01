ALTER TABLE organisation ADD COLUMN date_of_incorporation DATETIME DEFAULT NULL;
ALTER TABLE organisation ADD COLUMN business_type varchar(255) DEFAULT NULL;
ALTER TABLE organisation ADD COLUMN organisation_number varchar(255) DEFAULT NULL;

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
ALTER TABLE executive_officer add constraint FK_executive_officer foreign key (organisation_id) references organisation(id);
ALTER TABLE sic_code add constraint FK_sic_code foreign key (organisation_id) references organisation(id);



