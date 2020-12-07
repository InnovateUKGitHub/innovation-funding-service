create TABLE heukar_partner_organisation (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  application_id BIGINT(20) NULL,
  organisation_type_id BIGINT(20) NULL,
  PRIMARY KEY (id),
  CONSTRAINT heukar_partner_organisation_application_fk
    FOREIGN KEY (application_id)
    REFERENCES application (id),
  CONSTRAINT heukar_partner_organisation_org_type_fk
    FOREIGN KEY (organisation_type_id)
    REFERENCES organisation_type (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

