-- IFS-10104: Add a new table for competition third party config
CREATE TABLE competition_third_party_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  terms_and_conditions_label varchar(255) DEFAULT NULL,
  terms_and_conditions_guidance longtext DEFAULT NULL,
  project_cost_guidance_url varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Update competition to have third party config mapping
ALTER TABLE competition ADD COLUMN competition_third_party_config_id BIGINT(20) DEFAULT NULL;
ALTER TABLE competition ADD UNIQUE KEY competition_third_party_config_id_UNIQUE (competition_third_party_config_id);
ALTER TABLE competition ADD CONSTRAINT fk_competition_third_party_config FOREIGN KEY(competition_third_party_config_id) REFERENCES competition_third_party_config(id);
