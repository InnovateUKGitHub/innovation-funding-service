create TABLE heukar_partner_organisation (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  application_id BIGINT(20) NULL,
  organisation_type ENUM('BUSINESS','RESEARCH', 'RTO', 'PUBLIC_SECTOR_OR_CHARITY') NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT heukar_partner_organisation_application_fk
    FOREIGN KEY (application_id)
    REFERENCES application (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;