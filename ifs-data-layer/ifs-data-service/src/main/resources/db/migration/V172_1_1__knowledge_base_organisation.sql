CREATE TABLE knowledge_base (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255),
  PRIMARY KEY (id),
  UNIQUE KEY knowledge_base_uk (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
