-- IFS-12517-HECP document upload

-- Competition config holds eoi evidence setup
CREATE TABLE `competition_eoi_evidence_config` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `evidence_required` BIT(1) DEFAULT FALSE,
    `evidence_title` VARCHAR(255) DEFAULT NULL,
    `evidence_guidance` LONGTEXT DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Competition config holds allowed eoi evidence file types
CREATE TABLE `eoi_evidence_config_file_type` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `competition_eoi_evidence_config_id` bigint(20) NOT NULL,
    `file_type_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`competition_eoi_evidence_config_id`,`file_type_id`),
    CONSTRAINT `competition_eoi_evidence_config_fk` FOREIGN KEY (`competition_eoi_evidence_config_id`) REFERENCES `competition_eoi_evidence_config` (`id`),
    CONSTRAINT `evidence_file_type_fk` FOREIGN KEY (`file_type_id`) REFERENCES `file_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Competition mapping for eoi evidence config
ALTER TABLE `competition` ADD COLUMN `competition_eoi_evidence_config_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE `competition` ADD UNIQUE KEY `competition_eoi_evidence_config_id_UNIQUE` (`competition_eoi_evidence_config_id`);
ALTER TABLE `competition` ADD CONSTRAINT `fk_competition_eoi_evidence_config` FOREIGN KEY(`competition_eoi_evidence_config_id`) REFERENCES `competition_eoi_evidence_config` (`id`);

-- Modify unique constraint to support allowed file types for eoi evidence
ALTER TABLE `file_type` DROP INDEX `name_uk`;
ALTER TABLE `file_type` ADD UNIQUE KEY `name_extension_UNIQUE` (`name`, `extension`);

-- Application eoi evidence response holds user uploads
CREATE TABLE `application_eoi_evidence_response` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` bigint(20) DEFAULT NULL,
    `organisation_id` bigint(20) NOT NULL,
    `file_entry_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `application_fk` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
    CONSTRAINT `organisation_fk` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
    CONSTRAINT `file_entry_fk` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- Modify enum to support new activity state for eoi evidence
ALTER TABLE `activity_state` MODIFY COLUMN `activity_type` enum('APPLICATION_ASSESSMENT','PROJECT_SETUP','PROJECT_SETUP_COMPANIES_HOUSE_DETAILS','PROJECT_SETUP_PROJECT_DETAILS','PROJECT_SETUP_MONITORING_OFFICER_ASSIGNMENT','PROJECT_SETUP_BANK_DETAILS','PROJECT_SETUP_FINANCE_CHECKS','PROJECT_SETUP_VIABILITY','PROJECT_SETUP_ELIGIBILITY','PROJECT_SETUP_SPEND_PROFILE','PROJECT_SETUP_GRANT_OFFER_LETTER','APPLICATION','ASSESSMENT_REVIEW','ASSESSMENT_INTERVIEW_PANEL','ASSESSMENT_INTERVIEW','SUPPORTER_ASSIGNMENT','PROJECT_SETUP_PAYMENT_MILESTONES','PROJECT_SETUP_FUNDING_RULES', 'APPLICATION_EOI_EVIDENCE_UPLOAD');
ALTER TABLE `activity_state` MODIFY COLUMN `state` enum('CREATED','PENDING','REJECTED','ACCEPTED','WITHDRAWN','OPEN','READY_TO_SUBMIT','SUBMITTED','VERIFIED','NOT_VERIFIED','ASSIGNED','NOT_ASSIGNED','NOT_APPLICABLE','NOT_APPLICABLE_INFORMED','CONFLICT_OF_INTEREST','HANDLED_OFFLINE','COMPLETED_OFFLINE','ON_HOLD', 'NOT_SUBMITTED');