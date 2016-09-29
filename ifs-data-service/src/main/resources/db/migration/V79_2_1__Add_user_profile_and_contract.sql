CREATE TABLE contract (
  id BIGINT(20) AUTO_INCREMENT,
  current BOOLEAN NOT NULL DEFAULT FALSE,
  text LONGTEXT NOT NULL,
  annex_1 LONGTEXT NOT NULL,
  annex_2 LONGTEXT NOT NULL,
  annex_3 LONGTEXT NOT NULL,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  PRIMARY KEY (id),

  CONSTRAINT contract_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user(id),
  CONSTRAINT contract_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user(id)
) DEFAULT CHARSET=utf8;


CREATE TABLE profile (
  id BIGINT(20) AUTO_INCREMENT,
  user_id BIGINT(20) NOT NULL UNIQUE,
  address_id BIGINT(20),
  skills_areas LONGTEXT,
  business_type ENUM('BUSINESS', 'ACADEMIC'),
  contract_id BIGINT(20),
  contract_signed_date DATETIME,

  -- auditable fields
  created_by BIGINT(20) NOT NULL,
  created_on DATETIME NOT NULL,
  modified_by BIGINT(20) NOT NULL,
  modified_on DATETIME NOT NULL,

  PRIMARY KEY (id),

  CONSTRAINT profile_user_to_user_fk FOREIGN KEY (user_id) REFERENCES user(id),
  CONSTRAINT profile_address_to_address_fk FOREIGN KEY (address_id) REFERENCES address(id),
  CONSTRAINT profile_contract_to_contract_fk FOREIGN KEY (contract_id) REFERENCES contract(id),

  CONSTRAINT profile_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user(id),
  CONSTRAINT profile_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user(id)
) DEFAULT CHARSET=utf8;
