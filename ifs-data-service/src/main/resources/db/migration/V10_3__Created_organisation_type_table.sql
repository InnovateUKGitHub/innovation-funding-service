CREATE TABLE organisation_type
(
    id BIGINT(20) PRIMARY KEY NOT NULL,
    name VARCHAR(255),
    parent_organisation_type_id BIGINT(20),
    CONSTRAINT FK_eh40v8iivh39la2bmmr6h82u5 FOREIGN KEY (parent_organisation_type_id) REFERENCES organisation_type (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
CREATE INDEX FK_eh40v8iivh39la2bmmr6h82u5 ON organisation_type (parent_organisation_type_id);


ALTER TABLE organisation
    ADD organisation_type_id BIGINT(20) NULL;
CREATE INDEX FK_syoqdheljsd92k1vtdjfae31m ON organisation (organisation_type_id);
ALTER TABLE organisation
    ADD CONSTRAINT FK_syoqdheljsd92k1vtdjfae31m FOREIGN KEY (organisation_type_id) REFERENCES organisation_type (id);




INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (1, 'Business', null);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (2, 'Research', null);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (3, 'Public Sector', null);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (4, 'Charity', null);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (5, 'Academic', 2);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (6, 'Research & technology organisation (RTO)', 2);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (7, 'Catapult', 2);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (8, 'Public sector research establishment', 2);
INSERT INTO organisation_type (id, name, parent_organisation_type_id) VALUES (9, 'Research council institute', 2);




--CREATE INDEX FK_syoqdheljsd92k1vtdjfae31m ON organisation (organisation_type_id);


UPDATE `organisation` SET `organisation_type_id`='1' WHERE `id`='1';
UPDATE `organisation` SET `organisation_type_id`='1' WHERE `id`='2';
UPDATE `organisation` SET `organisation_type_id`='1' WHERE `id`='3';
UPDATE `organisation` SET `organisation_type_id`='1' WHERE `id`='4';
UPDATE `organisation` SET `organisation_type_id`='5' WHERE `id`='5';
UPDATE `organisation` SET `organisation_type_id`='5' WHERE `id`='6';
UPDATE `organisation` SET `organisation_type_id`='2' WHERE `id`='7';
