CREATE TABLE docusign_document (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  recipient_id bigint(20) NOT NULL,
  envelope_id varchar(255),
  signed_document_imported DATETIME,
  type enum('SIGNED_GRANT_OFFER_LETTER') NOT NULL,
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,
  KEY docusign_document_created_by_to_user_fk (created_by),
  KEY docusign_document_modified_by_to_user_fk (modified_by),
  CONSTRAINT docusign_document_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id),
  CONSTRAINT docusign_document_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE project ADD COLUMN signed_gol_docusign_document_id bigint(20);
ALTER TABLE project ADD CONSTRAINT fk_project_to_docusign_documnet FOREIGN KEY (signed_gol_docusign_document_id) REFERENCES docusign_document(id);


ALTER TABLE competition ADD COLUMN use_docusign_for_grant_offer_letter BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE project ADD COLUMN use_docusign_for_grant_offer_letter BOOLEAN NOT NULL DEFAULT FALSE;

-- IFS-3998 Insert scheduled job for DOI expiry.
insert into schedule_status (job_name, version) VALUES ('DOCUSIGN_IMPORT', now());