CREATE TABLE sil_message (
                             id VARCHAR(45) NOT NULL,
                             date_created DATETIME NOT NULL,
                             payload_type ENUM('APPLICATION_SUBMISSION','CONTACT','ASSESSMENT_COMPLETE','APPLICATION_UPDATE','USER_UPDATE') NOT NULL,
                             key_type  ENUM('COMPETITION_ID','APPLICATION_ID','USER_ID') NOT NULL,
                             key_value VARCHAR(45)  NULL,
                             payload VARCHAR(65000) NOT NULL,
                             response_code VARCHAR(45)  NULL,
                             PRIMARY KEY (id));