
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `academic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=964 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_state` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_type` enum('APPLICATION_ASSESSMENT','PROJECT_SETUP','PROJECT_SETUP_COMPANIES_HOUSE_DETAILS','PROJECT_SETUP_PROJECT_DETAILS','PROJECT_SETUP_MONITORING_OFFICER_ASSIGNMENT','PROJECT_SETUP_BANK_DETAILS','PROJECT_SETUP_FINANCE_CHECKS','PROJECT_SETUP_VIABILITY','PROJECT_SETUP_ELIGIBILITY','PROJECT_SETUP_SPEND_PROFILE','PROJECT_SETUP_GRANT_OFFER_LETTER','APPLICATION','ASSESSMENT_REVIEW','ASSESSMENT_INTERVIEW_PANEL','ASSESSMENT_INTERVIEW') NOT NULL,
  `state` enum('CREATED','PENDING','REJECTED','ACCEPTED','WITHDRAWN','OPEN','READY_TO_SUBMIT','SUBMITTED','VERIFIED','NOT_VERIFIED','ASSIGNED','NOT_ASSIGNED','NOT_APPLICABLE','NOT_APPLICABLE_INFORMED','CONFLICT_OF_INTEREST') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `address_line3` varchar(255) DEFAULT NULL,
  `town` varchar(255) DEFAULT NULL,
  `postcode` varchar(255) DEFAULT NULL,
  `county` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `affiliation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `affiliation_type` enum('EMPLOYER','PROFESSIONAL','PERSONAL','PERSONAL_FINANCIAL','FAMILY','FAMILY_FINANCIAL') NOT NULL,
  `affiliation_exists` tinyint(1) NOT NULL,
  `relation` varchar(255) DEFAULT NULL,
  `organisation` varchar(255) DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `description` text,
  `created_by` bigint(20) NOT NULL,
  `created_on` datetime NOT NULL,
  `modified_on` datetime NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `affiliation_user_to_user_fk` (`user_id`),
  KEY `affiliation_created_by_to_user_fk` (`created_by`),
  KEY `affiliation_modified_by_to_user_fk` (`modified_by`),
  CONSTRAINT `affiliation_created_by_to_user_fk` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `affiliation_modified_by_to_user_fk` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`),
  CONSTRAINT `affiliation_user_to_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agreement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `current` tinyint(1) NOT NULL DEFAULT '0',
  `text` longtext NOT NULL,
  `created_by` bigint(20) NOT NULL,
  `created_on` datetime NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modified_on` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `agreement_created_by_to_user_fk` (`created_by`),
  KEY `agreement_modified_by_to_user_fk` (`modified_by`),
  CONSTRAINT `agreement_created_by_to_user_fk` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `agreement_modified_by_to_user_fk` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `valid_from_date` datetime NOT NULL,
  `valid_to_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `duration_in_months` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `competition` bigint(20) DEFAULT NULL,
  `submitted_date` datetime DEFAULT NULL,
  `funding_decision` varchar(16) DEFAULT NULL,
  `assessor_feedback_file_entry_id` bigint(20) DEFAULT NULL,
  `completion` decimal(5,2) NOT NULL DEFAULT '0.00',
  `state_aid_agreed` tinyint(1) DEFAULT NULL,
  `resubmission` bit(1) DEFAULT NULL,
  `previous_application_number` varchar(255) DEFAULT NULL,
  `previous_application_title` varchar(255) DEFAULT NULL,
  `no_innovation_area_applicable` bit(1) NOT NULL DEFAULT b'0',
  `manage_funding_email_date` datetime DEFAULT NULL,
  `in_assessment_review_panel` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_r6qpl12jw4qehsirycsa416ka` (`competition`),
  KEY `application_ibfk_1` (`assessor_feedback_file_entry_id`),
  KEY `application_in_assessment_review_panel_idx` (`in_assessment_review_panel`),
  CONSTRAINT `FK_r6qpl12jw4qehsirycsa416ka` FOREIGN KEY (`competition`) REFERENCES `competition` (`id`),
  CONSTRAINT `application_ibfk_1` FOREIGN KEY (`assessor_feedback_file_entry_id`) REFERENCES `file_entry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_finance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  `finance_file_entry_id` bigint(20) DEFAULT NULL,
  `organisation_size_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_application_id_organisation_id` (`application_id`,`organisation_id`),
  KEY `FK_53xvxooxtgbfppaln9leak72m` (`application_id`),
  KEY `FK_98i98ljbqxb2yp5bok9pu5wcc` (`organisation_id`),
  KEY `application_finance_organisation_size_fk` (`organisation_size_id`),
  CONSTRAINT `FK_53xvxooxtgbfppaln9leak72m` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_98i98ljbqxb2yp5bok9pu5wcc` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `application_finance_organisation_size_fk` FOREIGN KEY (`organisation_size_id`) REFERENCES `organisation_size` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessor_count_option` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `competition_type_id` bigint(20) NOT NULL COMMENT 'Competition Type Id.',
  `option_name` varchar(100) NOT NULL COMMENT 'Assessor option name which can be used as a label in the front end. This give us flexibility to use different values for label and submission.',
  `option_value` int(4) NOT NULL COMMENT 'Assessor option value to be used for any business logic.',
  `default_option` tinyint(1) DEFAULT '0' COMMENT 'Is this option to be shown as selected by default.',
  PRIMARY KEY (`id`),
  KEY `competition_type_id_assessor_count_idx` (`competition_type_id`),
  CONSTRAINT `FK_assessor_count_option_competition_type` FOREIGN KEY (`competition_type_id`) REFERENCES `competition_type` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8 COMMENT='Table to store the options for the assessor that are used for various competition types.';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessor_form_input_response` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` longtext,
  `assessment_id` bigint(20) NOT NULL,
  `form_input_id` bigint(20) NOT NULL,
  `updated_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_trvg408xi9u5qcqxfwxuysk4l` (`assessment_id`),
  KEY `FK_dxcce29i6ofhm1v154xwrljtv` (`form_input_id`),
  CONSTRAINT `FK_dxcce29i6ofhm1v154xwrljtv` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`),
  CONSTRAINT `FK_trvg408xi9u5qcqxfwxuysk4l` FOREIGN KEY (`assessment_id`) REFERENCES `process` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uploader_id` bigint(20) NOT NULL,
  `file_entry_id` bigint(20) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_entry_UNIQUE` (`file_entry_id`),
  KEY `attachment_uploader_fk_idx` (`uploader_id`),
  CONSTRAINT `attachment_fileEntry_fk` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`),
  CONSTRAINT `attachment_uploader_fk` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sort_code` varchar(6) COLLATE utf8_bin NOT NULL,
  `account_number` varchar(8) COLLATE utf8_bin NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `organisation_address_id` bigint(20) NOT NULL,
  `organisation_id` bigint(20) NOT NULL,
  `company_name_score` tinyint(1) DEFAULT NULL,
  `registration_number_matched` bit(1) DEFAULT NULL,
  `address_score` tinyint(1) DEFAULT NULL,
  `manual_approval` bit(1) DEFAULT NULL,
  `verified` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_bank_details_org_and_project` (`project_id`,`organisation_id`),
  KEY `bank_details_to_organisation_address_fk` (`organisation_address_id`),
  KEY `bank_details_to_organisation_fk` (`organisation_id`),
  CONSTRAINT `bank_details_to_organisation_address_fk` FOREIGN KEY (`organisation_address_id`) REFERENCES `organisation_address` (`id`),
  CONSTRAINT `bank_details_to_organisation_fk` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `bank_details_to_project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `description` mediumtext,
  `priority` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_81thrbnb8c08gua7tvqj7xdqk` (`parent_id`),
  CONSTRAINT `FK_81thrbnb8c08gua7tvqj7xdqk` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) DEFAULT NULL,
  `class_pk` bigint(20) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_3kai632y5lw33gxmec08p54s0` (`category_id`),
  KEY `category_link_class_pk_idx` (`class_pk`),
  CONSTRAINT `FK_3kai632y5lw33gxmec08p54s0` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `competition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `max_research_ratio` int(11) DEFAULT '0',
  `academic_grant_percentage` int(11) DEFAULT '0',
  `budget_code` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `paf_code` varchar(255) DEFAULT NULL,
  `executive_user_id` bigint(20) DEFAULT NULL,
  `lead_technologist_user_id` bigint(20) DEFAULT NULL,
  `competition_type_id` bigint(20) DEFAULT NULL,
  `activity_code` varchar(255) DEFAULT NULL,
  `innovate_budget` varchar(255) DEFAULT NULL,
  `multi_stream` tinyint(1) NOT NULL DEFAULT '0',
  `collaboration_level` varchar(255) DEFAULT NULL,
  `stream_name` varchar(255) DEFAULT NULL,
  `resubmission` tinyint(1) DEFAULT NULL,
  `setup_complete` bit(1) DEFAULT NULL,
  `full_application_finance` bit(1) DEFAULT b'1',
  `assessor_count` int(4) DEFAULT '0',
  `assessor_pay` decimal(10,2) DEFAULT '0.00',
  `has_assessment_panel` bit(1) DEFAULT NULL,
  `has_interview_stage` bit(1) DEFAULT NULL,
  `assessor_finance_view` enum('OVERVIEW','DETAILED') NOT NULL DEFAULT 'OVERVIEW',
  `template` bit(1) DEFAULT b'0',
  `use_resubmission_question` bit(1) NOT NULL DEFAULT b'1',
  `non_ifs` bit(1) NOT NULL DEFAULT b'0',
  `non_ifs_url` varchar(255) DEFAULT NULL,
  `terms_and_conditions_id` bigint(20) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_oyhemo48a8fegie1npbk759s2` (`executive_user_id`),
  KEY `FK_7gcrp1ms5k4o9ehrci3uqx6hg` (`lead_technologist_user_id`),
  KEY `FK_4ymkkm30gi0r9w65d1xuawyws` (`competition_type_id`),
  KEY `terms_and_conditions_fk` (`terms_and_conditions_id`),
  CONSTRAINT `FK_4ymkkm30gi0r9w65d1xuawyws` FOREIGN KEY (`competition_type_id`) REFERENCES `competition_type` (`id`),
  CONSTRAINT `FK_7gcrp1ms5k4o9ehrci3uqx6hg` FOREIGN KEY (`lead_technologist_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_oyhemo48a8fegie1npbk759s2` FOREIGN KEY (`executive_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `terms_and_conditions_fk` FOREIGN KEY (`terms_and_conditions_id`) REFERENCES `terms_and_conditions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `competition_funder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `funder` varchar(255) DEFAULT NULL,
  `funder_budget` bigint(20) DEFAULT '0',
  `competition_id` bigint(20) NOT NULL,
  `co_funder` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `competition_funders_competitions_id_idx` (`competition_id`),
  CONSTRAINT `competition_funders_competitions_id_idx` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `competition_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `state_aid` bit(1) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `template_competition_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `template_competition_fk` (`template_competition_id`),
  CONSTRAINT `template_competition_fk` FOREIGN KEY (`template_competition_id`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `competition_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(20) DEFAULT NULL,
  `competition_role` enum('ASSESSOR','INNOVATION_LEAD','PANEL_ASSESSOR','INTERVIEW_ASSESSOR') COLLATE utf8_bin NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `invite_id` bigint(20) DEFAULT NULL,
  `rejection_reason_id` bigint(20) DEFAULT NULL,
  `rejection_comment` longtext COLLATE utf8_bin,
  `participant_status_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_role_user_competition` (`competition_id`,`user_id`,`competition_role`,`invite_id`),
  KEY `competition_user_to_user_fk` (`user_id`),
  KEY `competition_user_to_rejection_reason_fk` (`rejection_reason_id`),
  KEY `competition_user_to_participant_status_fk` (`participant_status_id`),
  CONSTRAINT `competition_user_to_competition_fk` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`),
  CONSTRAINT `competition_user_to_participant_status_fk` FOREIGN KEY (`participant_status_id`) REFERENCES `participant_status` (`id`),
  CONSTRAINT `competition_user_to_rejection_reason_fk` FOREIGN KEY (`rejection_reason_id`) REFERENCES `rejection_reason` (`id`),
  CONSTRAINT `competition_user_to_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `public_content_id` bigint(20) NOT NULL,
  `date` datetime DEFAULT NULL,
  `content` longtext,
  PRIMARY KEY (`id`),
  KEY `FK_content_event_to_public_content` (`public_content_id`),
  CONSTRAINT `FK_content_event_to_public_content` FOREIGN KEY (`public_content_id`) REFERENCES `public_content` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content_section_id` bigint(20) NOT NULL,
  `heading` varchar(255) DEFAULT NULL,
  `content` longtext,
  `file_entry_id` bigint(20) DEFAULT NULL,
  `priority` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_content_group_to_content_section` (`content_section_id`),
  KEY `FK_content_group_to_file_entry` (`file_entry_id`),
  CONSTRAINT `FK_content_group_to_content_section` FOREIGN KEY (`content_section_id`) REFERENCES `content_section` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_content_group_to_file_entry` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_section` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `public_content_id` bigint(20) NOT NULL,
  `status` enum('IN_PROGRESS','COMPLETE') DEFAULT NULL,
  `type` enum('SEARCH','SUMMARY','ELIGIBILITY','SCOPE','DATES','HOW_TO_APPLY','SUPPORTING_INFORMATION') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_content_section_to_public_content` (`public_content_id`),
  CONSTRAINT `FK_content_section_to_public_content` FOREIGN KEY (`public_content_id`) REFERENCES `public_content` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` decimal(14,2) DEFAULT NULL,
  `cost_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ae4qyairso4xgum92xhti20xm` (`cost_group_id`),
  CONSTRAINT `FK_ae4qyairso4xgum92xhti20xm` FOREIGN KEY (`cost_group_id`) REFERENCES `cost_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_categorization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cost_id` bigint(20) NOT NULL,
  `cost_category_id` bigint(20) NOT NULL,
  `priority` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_cost_cost_category` (`cost_id`,`cost_category_id`),
  KEY `FK_8mmq5unyree8h9f3l30fmw579` (`cost_category_id`),
  KEY `FK_eaqwxt1nx926138w5wkmfs1gt` (`cost_id`),
  CONSTRAINT `FK_8mmq5unyree8h9f3l30fmw579` FOREIGN KEY (`cost_category_id`) REFERENCES `cost_category` (`id`),
  CONSTRAINT `FK_eaqwxt1nx926138w5wkmfs1gt` FOREIGN KEY (`cost_id`) REFERENCES `cost` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `cost_category_group_id` bigint(20) NOT NULL,
  `label` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_py0mpvk8bt1klihl44whkqmp5` (`cost_category_group_id`),
  CONSTRAINT `FK_py0mpvk8bt1klihl44whkqmp5` FOREIGN KEY (`cost_category_group_id`) REFERENCES `cost_category_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_category_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_category_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `cost_category_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gacypps10lfd1wqy6v31r5eoo` (`cost_category_group_id`),
  CONSTRAINT `FK_gacypps10lfd1wqy6v31r5eoo` FOREIGN KEY (`cost_category_group_id`) REFERENCES `cost_category_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_time_period` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `duration_amount` int(11) NOT NULL,
  `duration_unit` varchar(255) NOT NULL,
  `offset_amount` int(11) NOT NULL,
  `offset_unit` varchar(255) NOT NULL,
  `cost_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7a1kluy79whovi3c0ik0wmrf0` (`cost_id`),
  CONSTRAINT `FK_7a1kluy79whovi3c0ik0wmrf0` FOREIGN KEY (`cost_id`) REFERENCES `cost` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ethnicity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `description` varchar(255) COLLATE utf8_bin NOT NULL,
  `priority` int(11) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `priority` (`priority`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filesize_bytes` bigint(20) NOT NULL,
  `media_type` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finance_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `organisation_id` bigint(20) NOT NULL,
  `cost_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_finance_check_project` (`project_id`),
  KEY `FK_finance_check_organisation` (`organisation_id`),
  KEY `FK_finance_check_cost_group` (`cost_group_id`),
  KEY `finance_check_project_organisation_idx` (`project_id`,`organisation_id`),
  CONSTRAINT `FK_finance_check_cost_group` FOREIGN KEY (`cost_group_id`) REFERENCES `cost_group` (`id`),
  CONSTRAINT `FK_finance_check_organisation` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_finance_check_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finance_row` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cost` double DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `target_id` bigint(20) NOT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  `application_row_id` bigint(20) DEFAULT NULL,
  `row_type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `row_type_application_row_unique` (`row_type`,`application_row_id`),
  KEY `FK_14n47e1gx72ud7hj3t2yscu1v` (`target_id`),
  KEY `FK_3ocl28vkv3coj1t5hmgixvl6` (`question_id`),
  KEY `finance_row_application_row_id_fk` (`application_row_id`),
  KEY `finance_row_application_row_id_idx` (`id`),
  CONSTRAINT `FK_3ocl28vkv3coj1t5hmgixvl6` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
  CONSTRAINT `finance_row_application_row_id_fk` FOREIGN KEY (`application_row_id`) REFERENCES `finance_row` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finance_row_meta_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finance_row_meta_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `finance_row_id` bigint(20) NOT NULL DEFAULT '0',
  `finance_row_meta_field_id` bigint(20) NOT NULL DEFAULT '0',
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_h6lijwiwnsblqurwxjftvdn7n` (`finance_row_meta_field_id`),
  KEY `FK_cryaaiuibh4b0sqw3aqrkspmb` (`finance_row_id`),
  CONSTRAINT `FK_cryaaiuibh4b0sqw3aqrkspmb` FOREIGN KEY (`finance_row_id`) REFERENCES `finance_row` (`id`),
  CONSTRAINT `FK_h6lijwiwnsblqurwxjftvdn7n` FOREIGN KEY (`finance_row_meta_field_id`) REFERENCES `finance_row_meta_field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_input` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `word_count` int(11) DEFAULT NULL,
  `form_input_type_id` bigint(20) NOT NULL,
  `competition_id` bigint(20) DEFAULT NULL,
  `included_in_application_summary` tinyint(1) NOT NULL DEFAULT '1',
  `description` varchar(255) DEFAULT NULL,
  `guidance_title` longtext,
  `guidance_answer` longtext,
  `priority` int(11) NOT NULL,
  `question_id` bigint(20) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `active` bit(1) NOT NULL DEFAULT b'1',
  `allowed_file_types` longtext,
  PRIMARY KEY (`id`),
  KEY `FK_pvbo288244dfas1gd12t17pkv` (`form_input_type_id`),
  KEY `FK_hgdynqsaela2nuwm41nohmxg1` (`competition_id`),
  KEY `form_input_to_question_fk` (`question_id`),
  KEY `form_input_scope_idx` (`scope`),
  KEY `form_input_active_idx` (`active`),
  CONSTRAINT `FK_hgdynqsaela2nuwm41nohmxg1` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`),
  CONSTRAINT `FK_pvbo288244dfas1gd12t17pkv` FOREIGN KEY (`form_input_type_id`) REFERENCES `form_input_type` (`id`),
  CONSTRAINT `form_input_to_question_fk` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=608 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_input_response` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `update_date` datetime DEFAULT NULL,
  `value` longtext,
  `form_input_id` bigint(20) DEFAULT NULL,
  `updated_by_id` bigint(20) DEFAULT NULL,
  `application_id` bigint(20) DEFAULT NULL,
  `file_entry_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_application_form_input` (`application_id`,`form_input_id`,`updated_by_id`),
  KEY `FK_7901a8ft9tx1e02r82t84feaj` (`form_input_id`),
  KEY `FK_e83s9n8p6d60v1on2730jf8m9` (`updated_by_id`),
  KEY `FK_fxu0iyfgby8uecsrpkld59j7n` (`application_id`),
  KEY `file_entry_id` (`file_entry_id`),
  CONSTRAINT `FK_7901a8ft9tx1e02r82t84feaj` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`),
  CONSTRAINT `FK_e83s9n8p6d60v1on2730jf8m9` FOREIGN KEY (`updated_by_id`) REFERENCES `process_role` (`id`),
  CONSTRAINT `FK_fxu0iyfgby8uecsrpkld59j7n` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `form_input_response_ibfk_1` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_input_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `form_input_type_title_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_input_validator` (
  `form_input_id` bigint(20) NOT NULL,
  `form_validator_id` bigint(20) NOT NULL,
  PRIMARY KEY (`form_input_id`,`form_validator_id`),
  KEY `FK_y95iwkay85fdurj700i6i188` (`form_validator_id`),
  CONSTRAINT `FK_lfojcj07kbeklgn9v0g0hg3sb` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`),
  CONSTRAINT `FK_y95iwkay85fdurj700i6i188` FOREIGN KEY (`form_validator_id`) REFERENCES `form_validator` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_validator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `clazz_name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grant_claim_maximum` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `organisation_size_id` bigint(20) DEFAULT NULL,
  `organisation_type_id` bigint(20) NOT NULL,
  `competition_type_id` bigint(20) NOT NULL,
  `maximum` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_grant_claim_maximum` (`category_id`,`organisation_size_id`,`organisation_type_id`,`competition_type_id`),
  KEY `grant_claim_maximum_organisation_size_fk` (`organisation_size_id`),
  KEY `grant_claim_maximum_organisation_type_fk` (`organisation_type_id`),
  KEY `grant_claim_maximum_competition_type_fk` (`competition_type_id`),
  CONSTRAINT `grant_claim_maximum_category_fk` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `grant_claim_maximum_competition_type_fk` FOREIGN KEY (`competition_type_id`) REFERENCES `competition_type` (`id`),
  CONSTRAINT `grant_claim_maximum_organisation_size_fk` FOREIGN KEY (`organisation_size_id`) REFERENCES `organisation_size` (`id`),
  CONSTRAINT `grant_claim_maximum_organisation_type_fk` FOREIGN KEY (`organisation_type_id`) REFERENCES `organisation_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=286 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guidance_row` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `form_input_id` bigint(20) NOT NULL,
  `subject` varchar(100) DEFAULT NULL,
  `justification` longtext,
  `priority` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_condition_form_input_idx` (`form_input_id`),
  CONSTRAINT `fk_condition_form_input` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=290 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `hash` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` enum('SENT','CREATED','OPENED') NOT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `innovation_category_id` bigint(20) DEFAULT NULL,
  `sent_by` bigint(20) DEFAULT NULL,
  `sent_on` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_unique_target_id_email` (`type`,`target_id`,`email`),
  UNIQUE KEY `uk_hash` (`hash`),
  KEY `invite_to_category_fk` (`innovation_category_id`),
  KEY `invite_sent_by_to_user_fk` (`sent_by`),
  CONSTRAINT `invite_sent_by_to_user_fk` FOREIGN KEY (`sent_by`) REFERENCES `user` (`id`),
  CONSTRAINT `invite_to_category_fk` FOREIGN KEY (`innovation_category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invite_organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `organisation_name` varchar(255) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ae3mvog2j5kdcilv57hwcokmr` (`organisation_id`),
  CONSTRAINT `FK_ae3mvog2j5kdcilv57hwcokmr` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keyword` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `public_content_id` bigint(20) NOT NULL,
  `keyword` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_keyword_to_public_content` (`public_content_id`),
  CONSTRAINT `FK_keyword_to_public_content` FOREIGN KEY (`public_content_id`) REFERENCES `public_content` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lead_applicant_type` (
  `competition_id` bigint(20) NOT NULL,
  `organisation_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`organisation_type_id`,`competition_id`),
  KEY `competition_id_fk_idx` (`competition_id`),
  CONSTRAINT `competition_id_fk` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `org_type_id_fk` FOREIGN KEY (`organisation_type_id`) REFERENCES `organisation_type` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `milestone` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `competition_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `milestone_unique_competition_type` (`type`,`competition_id`),
  KEY `FK_d2gmngr50hf7lkjv8s9mxhfms` (`competition_id`),
  CONSTRAINT `FK_d2gmngr50hf7lkjv8s9mxhfms` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `monitoring_officer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_project_id_UNIQUE` (`project_id`),
  KEY `monitoring_officer_to_project_fk` (`project_id`),
  CONSTRAINT `monitoring_officer_to_project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `company_house_number` varchar(255) DEFAULT NULL,
  `organisation_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_syoqdheljsd92k1vtdjfae31m` (`organisation_type_id`),
  CONSTRAINT `FK_syoqdheljsd92k1vtdjfae31m` FOREIGN KEY (`organisation_type_id`) REFERENCES `organisation_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_id` bigint(20) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  `address_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g3y4ooi9akaq8e98efgmljigm` (`organisation_id`,`address_id`),
  KEY `FK_prle5vffxxpi4ibqymh6ic87x` (`address_id`),
  CONSTRAINT `FK_k8ipyjlxpsqfga85v2vhg0m0x` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_prle5vffxxpi4ibqymh6ic87x` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation_size` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation_type` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `parent_organisation_type_id` bigint(20) DEFAULT NULL,
  `visible_in_setup` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_eh40v8iivh39la2bmmr6h82u5` (`parent_organisation_type_id`),
  CONSTRAINT `FK_eh40v8iivh39la2bmmr6h82u5` FOREIGN KEY (`parent_organisation_type_id`) REFERENCES `organisation_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `participant_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partner_organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `organisation_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `lead_organisation` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_b3476se7may81i65fpmjt2jte` (`project_id`,`organisation_id`),
  KEY `FK_fpatn7eo4gdhqi5tej589n7wk` (`organisation_id`),
  CONSTRAINT `FK_fpatn7eo4gdhqi5tej589n7wk` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_h4cpvkntxf8g7mp4i6fe6eoon` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `thread_id` bigint(20) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  `body` text COLLATE utf8_bin NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `post_thread_fk` (`thread_id`),
  KEY `post_author_fk` (`author_id`),
  CONSTRAINT `post_author_fk` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `post_thread_fk` FOREIGN KEY (`thread_id`) REFERENCES `thread` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post_attachment` (
  `post_id` bigint(20) NOT NULL,
  `attachment_id` bigint(20) NOT NULL,
  PRIMARY KEY (`post_id`,`attachment_id`),
  KEY `post_attachment_attachment_fk` (`attachment_id`),
  CONSTRAINT `post_attachment_attachment_fk` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`),
  CONSTRAINT `post_attachment_post_fk` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `event` varchar(255) DEFAULT NULL,
  `last_modified` datetime NOT NULL,
  `start_date` date DEFAULT NULL,
  `process_type` varchar(31) NOT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  `participant_id` bigint(20) DEFAULT NULL,
  `activity_state_id` bigint(20) NOT NULL,
  `internal_participant_id` bigint(20) DEFAULT NULL,
  `version` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `process_to_activity_state_fk` (`activity_state_id`),
  KEY `internal_participant_fk` (`internal_participant_id`),
  KEY `process_process_type_idx` (`process_type`),
  KEY `process_participant_id_idx` (`participant_id`),
  KEY `process_target_id_idx` (`target_id`),
  CONSTRAINT `internal_participant_fk` FOREIGN KEY (`internal_participant_id`) REFERENCES `user` (`id`),
  CONSTRAINT `process_to_activity_state_fk` FOREIGN KEY (`activity_state_id`) REFERENCES `activity_state` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_outcome` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` longtext,
  `description` longtext,
  `outcome` varchar(255) DEFAULT NULL,
  `outcome_type` varchar(255) DEFAULT NULL,
  `process_id` bigint(20) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rm72g2d5hsse93bn54jimfkbw` (`process_id`),
  KEY `process_outcome_created_by_to_user_fk` (`created_by`),
  KEY `process_outcome_modified_by_to_user_fk` (`modified_by`),
  CONSTRAINT `FK_rm72g2d5hsse93bn54jimfkbw` FOREIGN KEY (`process_id`) REFERENCES `process` (`id`),
  CONSTRAINT `process_outcome_created_by_to_user_fk` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `process_outcome_modified_by_to_user_fk` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gwtw85iv3vxq2914vxbluc8e9` (`application_id`),
  KEY `FK_20gvkjd4xrjyspmlisrd50xbj` (`organisation_id`),
  KEY `FK_j0syxe9gnfpvde1f6mqtul154` (`role_id`),
  KEY `FK_gm7bql0vdig803ktf5pc5mo2b` (`user_id`),
  CONSTRAINT `FK_20gvkjd4xrjyspmlisrd50xbj` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_gm7bql0vdig803ktf5pc5mo2b` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_gwtw85iv3vxq2914vxbluc8e9` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_j0syxe9gnfpvde1f6mqtul154` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_id` bigint(20) DEFAULT NULL,
  `skills_areas` longtext,
  `business_type` enum('BUSINESS','ACADEMIC') DEFAULT NULL,
  `agreement_id` bigint(20) DEFAULT NULL,
  `agreement_signed_date` datetime DEFAULT NULL,
  `created_by` bigint(20) NOT NULL,
  `created_on` datetime NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modified_on` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `profile_address_to_address_fk` (`address_id`),
  KEY `profile_created_by_to_user_fk` (`created_by`),
  KEY `profile_modified_by_to_user_fk` (`modified_by`),
  KEY `profile_agreement_to_agreement_fk` (`agreement_id`),
  CONSTRAINT `profile_address_to_address_fk` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `profile_agreement_to_agreement_fk` FOREIGN KEY (`agreement_id`) REFERENCES `agreement` (`id`),
  CONSTRAINT `profile_created_by_to_user_fk` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `profile_modified_by_to_user_fk` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `duration_in_months` bigint(20) DEFAULT NULL,
  `address` bigint(20) DEFAULT NULL,
  `target_start_date` date DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `application_id` bigint(20) NOT NULL,
  `collaboration_agreement_file_entry_id` bigint(20) DEFAULT NULL,
  `exploitation_plan_file_entry_id` bigint(20) DEFAULT NULL,
  `documents_submitted_date` datetime DEFAULT NULL,
  `other_documents_approved` enum('UNSET','APPROVED','REJECTED') COLLATE utf8_bin NOT NULL DEFAULT 'UNSET',
  `grant_offer_letter_file_entry_id` bigint(20) DEFAULT NULL,
  `additional_contract_file_entry_id` bigint(20) DEFAULT NULL,
  `signed_grant_offer_file_entry_id` bigint(20) DEFAULT NULL,
  `offer_submitted_date` datetime DEFAULT NULL,
  `grant_offer_letter_rejection_reason` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `spend_profile_submitted_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_one_project_per_application` (`application_id`),
  KEY `project_ibfk_1` (`collaboration_agreement_file_entry_id`),
  KEY `project_ibfk_2` (`exploitation_plan_file_entry_id`),
  KEY `fk_project_address` (`address`),
  CONSTRAINT `fk_project_address` FOREIGN KEY (`address`) REFERENCES `address` (`id`),
  CONSTRAINT `project_application_fk` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `project_ibfk_1` FOREIGN KEY (`collaboration_agreement_file_entry_id`) REFERENCES `file_entry` (`id`),
  CONSTRAINT `project_ibfk_2` FOREIGN KEY (`exploitation_plan_file_entry_id`) REFERENCES `file_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_finance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `organisation_id` bigint(20) NOT NULL,
  `viability_status` enum('UNSET','GREEN','AMBER','RED') COLLATE utf8_bin NOT NULL DEFAULT 'UNSET',
  `credit_report_confirmed` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Whether an available credit report has been confirmed during Viability checks',
  `eligibility_status` enum('UNSET','GREEN','AMBER','RED') COLLATE utf8_bin NOT NULL DEFAULT 'UNSET',
  `organisation_size_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_project_finance_to_project_idx` (`project_id`),
  KEY `FK_project_finance_to_organisation_idx` (`organisation_id`),
  KEY `project_finance_organisation_size_fk` (`organisation_size_id`),
  CONSTRAINT `FK_project_finance_to_organisation` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_project_finance_to_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `project_finance_organisation_size_fk` FOREIGN KEY (`organisation_size_id`) REFERENCES `organisation_size` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `project_role` varchar(255) NOT NULL,
  `invite_id` bigint(20) DEFAULT NULL,
  `participant_status_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `project_user_to_project_fk` (`project_id`),
  KEY `project_user_to_organisation_fk` (`organisation_id`),
  KEY `project_user_to_user_fk` (`user_id`),
  KEY `project_user_to_projet_role_fk` (`project_role`),
  KEY `project_user_to_invite_fk` (`invite_id`),
  KEY `project_user_to_participant_status_fk` (`participant_status_id`),
  CONSTRAINT `project_user_to_invite_fk` FOREIGN KEY (`invite_id`) REFERENCES `invite` (`id`),
  CONSTRAINT `project_user_to_organisation_fk` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `project_user_to_participant_status_fk` FOREIGN KEY (`participant_status_id`) REFERENCES `participant_status` (`id`),
  CONSTRAINT `project_user_to_project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `project_user_to_projet_role_fk` FOREIGN KEY (`project_role`) REFERENCES `project_role` (`name`),
  CONSTRAINT `project_user_to_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `public_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(20) NOT NULL,
  `publish_date` datetime DEFAULT NULL,
  `short_description` varchar(255) DEFAULT NULL,
  `project_funding_range` varchar(255) DEFAULT NULL,
  `eligibility_summary` longtext,
  `summary` longtext,
  `funding_type` enum('GRANT','LOAN','PROCUREMENT') DEFAULT NULL,
  `project_size` varchar(255) DEFAULT NULL,
  `invite_only` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_public_content_to_competition` (`competition_id`),
  CONSTRAINT `FK_public_content_to_competition` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assign_enabled` bit(1) DEFAULT NULL,
  `description` longtext,
  `mark_as_completed_enabled` bit(1) DEFAULT NULL,
  `multiple_statuses` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `question_number` varchar(255) DEFAULT NULL,
  `competition_id` bigint(20) DEFAULT NULL,
  `section_id` bigint(20) DEFAULT NULL,
  `assessor_maximum_score` int(11) DEFAULT NULL,
  `question_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_hgdynqsaela2nuwm41nohmxg0` (`competition_id`),
  KEY `FK_dyisjpgv8bmnrhy8t72lloed3` (`section_id`),
  CONSTRAINT `FK_dyisjpgv8bmnrhy8t72lloed3` FOREIGN KEY (`section_id`) REFERENCES `section` (`id`),
  CONSTRAINT `FK_hgdynqsaela2nuwm41nohmxg0` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assigned_date` datetime DEFAULT NULL,
  `marked_as_complete` bit(1) DEFAULT NULL,
  `notified` bit(1) DEFAULT NULL,
  `application_id` bigint(20) DEFAULT NULL,
  `assigned_by_id` bigint(20) DEFAULT NULL,
  `assignee_id` bigint(20) DEFAULT NULL,
  `marked_as_complete_by_id` bigint(20) DEFAULT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_application_question_assignee` (`application_id`,`question_id`,`assignee_id`),
  UNIQUE KEY `question_status_complete_unique` (`application_id`,`marked_as_complete_by_id`,`question_id`),
  KEY `FK_9tevxfhuvyt035mdum4mb2eiu` (`application_id`),
  KEY `FK_9e9m1rm34n2jredw4vf3w28vx` (`assigned_by_id`),
  KEY `FK_h3hna6d57cxximuty8600kysj` (`assignee_id`),
  KEY `FK_plbuqijqpe0e69q122a0b5i03` (`marked_as_complete_by_id`),
  KEY `FK_thcio7r5atcoq9xbisqi3yq9y` (`question_id`),
  CONSTRAINT `FK_9e9m1rm34n2jredw4vf3w28vx` FOREIGN KEY (`assigned_by_id`) REFERENCES `process_role` (`id`),
  CONSTRAINT `FK_9tevxfhuvyt035mdum4mb2eiu` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_h3hna6d57cxximuty8600kysj` FOREIGN KEY (`assignee_id`) REFERENCES `process_role` (`id`),
  CONSTRAINT `FK_plbuqijqpe0e69q122a0b5i03` FOREIGN KEY (`marked_as_complete_by_id`) REFERENCES `process_role` (`id`),
  CONSTRAINT `FK_thcio7r5atcoq9xbisqi3yq9y` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rejection_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `reason` varchar(255) NOT NULL,
  `priority` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `section` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assessor_guidance_description` longtext,
  `description` longtext,
  `display_in_assessment_application_summary` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `competition_id` bigint(20) DEFAULT NULL,
  `parent_section_id` bigint(20) DEFAULT NULL,
  `question_group` bit(1) NOT NULL,
  `section_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_525cnqb0rlfvo4ixk42jqvcxv` (`competition_id`),
  KEY `FK_pe8s9ptfql4li2ergosi21q9b` (`parent_section_id`),
  CONSTRAINT `FK_525cnqb0rlfvo4ixk42jqvcxv` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`),
  CONSTRAINT `FK_pe8s9ptfql4li2ergosi21q9b` FOREIGN KEY (`parent_section_id`) REFERENCES `section` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `setup_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `completed` bit(1) NOT NULL,
  `class_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `class_pk` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  `target_class_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_setup_status_idx` (`parent_id`),
  KEY `setup_status_class_pkx` (`class_pk`),
  KEY `setup_status_class_name_idx` (`class_name`),
  KEY `setup_status_target_idx` (`target_id`),
  KEY `setup_status_target_class_name_idx` (`target_class_name`),
  CONSTRAINT `FK_setup_status_id` FOREIGN KEY (`parent_id`) REFERENCES `setup_status` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spend_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cost_category_type_id` bigint(20) NOT NULL,
  `eligible_costs_cost_group_id` bigint(20) NOT NULL,
  `organisation_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `spend_profile_figures_cost_group_id` bigint(20) NOT NULL,
  `marked_as_complete` bit(1) DEFAULT NULL,
  `generated_date` datetime NOT NULL,
  `generated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_project_org` (`project_id`,`organisation_id`),
  KEY `FK_260indgab3foqj3wd1br5ppsx` (`cost_category_type_id`),
  KEY `FK_gwv7qqsmomu0vf3aihpchw4ya` (`eligible_costs_cost_group_id`),
  KEY `FK_t09amptdpq28to3ndm4sbj0pr` (`organisation_id`),
  KEY `FK_dgtosuo14i9xovfh7ja16io9l` (`project_id`),
  KEY `FK_tp4phg304cqs0f8sfpjqt5cvh` (`spend_profile_figures_cost_group_id`),
  KEY `generated_by_fk` (`generated_by_id`),
  CONSTRAINT `FK_260indgab3foqj3wd1br5ppsx` FOREIGN KEY (`cost_category_type_id`) REFERENCES `cost_category_type` (`id`),
  CONSTRAINT `FK_dgtosuo14i9xovfh7ja16io9l` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_gwv7qqsmomu0vf3aihpchw4ya` FOREIGN KEY (`eligible_costs_cost_group_id`) REFERENCES `cost_group` (`id`),
  CONSTRAINT `FK_t09amptdpq28to3ndm4sbj0pr` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_tp4phg304cqs0f8sfpjqt5cvh` FOREIGN KEY (`spend_profile_figures_cost_group_id`) REFERENCES `cost_group` (`id`),
  CONSTRAINT `generated_by_fk` FOREIGN KEY (`generated_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `terms_and_conditions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `template` varchar(255) COLLATE utf8_bin NOT NULL,
  `version` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `terms_and_conditions_UNIQUE` (`name`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thread` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_pk` bigint(20) NOT NULL,
  `class_name` varchar(255) COLLATE utf8_bin NOT NULL,
  `thread_type` varchar(45) COLLATE utf8_bin NOT NULL,
  `title` varchar(255) COLLATE utf8_bin NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `section` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `closed_by_user_id` bigint(20) DEFAULT NULL,
  `closed_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `closed_by_user_id_fk` (`closed_by_user_id`),
  CONSTRAINT `closed_by_user_id_fk` FOREIGN KEY (`closed_by_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) DEFAULT NULL,
  `class_pk` bigint(20) DEFAULT NULL,
  `extra_info` longtext,
  `hash` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mtjr5jkw9dpbqhgx3mu1bomlt` (`hash`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `invite_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `uid` varchar(255) NOT NULL,
  `system_user` tinyint(1) DEFAULT '0',
  `gender` enum('MALE','FEMALE','NOT_STATED') DEFAULT NULL,
  `disability` enum('YES','NO','NOT_STATED') DEFAULT NULL,
  `ethnicity_id` bigint(20) DEFAULT NULL,
  `profile_id` bigint(20) DEFAULT NULL,
  `allow_marketing_emails` tinyint(1) NOT NULL DEFAULT '0',
  `created_by` bigint(20) NOT NULL,
  `created_on` datetime NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modified_on` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `profile_id` (`profile_id`),
  KEY `user_to_ethnicity_fk` (`ethnicity_id`),
  KEY `uid_idx` (`uid`),
  KEY `user_created_by_to_user_fk` (`created_by`),
  KEY `user_modified_by_to_user_fk` (`modified_by`),
  CONSTRAINT `user_created_by_to_user_fk` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  CONSTRAINT `user_modified_by_to_user_fk` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`),
  CONSTRAINT `user_profile_to_profile_fk` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`),
  CONSTRAINT `user_to_ethnicity_fk` FOREIGN KEY (`ethnicity_id`) REFERENCES `ethnicity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_organisation` (
  `user_id` bigint(20) NOT NULL,
  `organisation_id` bigint(20) NOT NULL,
  KEY `FK_hovbl4knvvbdxktjlkkxnbuh0` (`organisation_id`),
  KEY `FK_kbg0lkwwyivtraa6pm155q9lb` (`user_id`),
  CONSTRAINT `FK_hovbl4knvvbdxktjlkkxnbuh0` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_kbg0lkwwyivtraa6pm155q9lb` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_it77eq964jhfqtu54081ebtio` (`role_id`),
  KEY `FK_apcc8lxk2xnug8377fatvbn04` (`user_id`),
  CONSTRAINT `FK_apcc8lxk2xnug8377fatvbn04` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_it77eq964jhfqtu54081ebtio` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verification_condition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `severity` varchar(255) DEFAULT NULL,
  `code` int(11) DEFAULT NULL,
  `description` longtext,
  `bank_details_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_condition_bank_details_idx` (`bank_details_id`),
  CONSTRAINT `fk_condition_bank_details` FOREIGN KEY (`bank_details_id`) REFERENCES `bank_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

