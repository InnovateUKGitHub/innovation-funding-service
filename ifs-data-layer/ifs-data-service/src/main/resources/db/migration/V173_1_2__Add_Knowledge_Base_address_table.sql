CREATE TABLE knowledge_base_address (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  address_id bigint(20) NOT NULL,
  knowledge_base_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT knowledge_base_address_to_application_fk FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_base (id),
  CONSTRAINT knowledge_base_address_to_address_fk FOREIGN KEY (address_id) REFERENCES address (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;