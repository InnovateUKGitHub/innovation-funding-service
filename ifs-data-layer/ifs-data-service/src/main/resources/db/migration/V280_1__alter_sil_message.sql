ALTER TABLE sil_message
    CHANGE COLUMN payload_type payload_type ENUM('APPLICATION_SUBMISSION', 'CONTACT','ASSESSMENT_COMPLETE','APPLICATION_UPDATE','USER_UPDATE','APPLICATION_LOCATION_INFO') NOT NULL;