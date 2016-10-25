-- MySQL dump 10.13  Distrib 5.6.24, for osx10.8 (x86_64)
--
-- Host: 127.0.0.1    Database: ifs
-- ------------------------------------------------------
-- Server version	5.6.25

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

--
-- Table structure for table `academic`
--

DROP TABLE IF EXISTS `academic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `academic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=964 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
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

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `duration_in_months` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `application_status_id` bigint(20) DEFAULT NULL,
  `competition` bigint(20) DEFAULT NULL,
  `submitted_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_mdoygpekookkkntrvgy67jjlb` (`application_status_id`),
  KEY `FK_r6qpl12jw4qehsirycsa416ka` (`competition`),
  CONSTRAINT `FK_mdoygpekookkkntrvgy67jjlb` FOREIGN KEY (`application_status_id`) REFERENCES `application_status` (`id`),
  CONSTRAINT `FK_r6qpl12jw4qehsirycsa416ka` FOREIGN KEY (`competition`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application_finance`
--

DROP TABLE IF EXISTS `application_finance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_finance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  `organisation_size` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_application_id_organisation_id` (`application_id`,`organisation_id`),
  KEY `FK_53xvxooxtgbfppaln9leak72m` (`application_id`),
  KEY `FK_98i98ljbqxb2yp5bok9pu5wcc` (`organisation_id`),
  CONSTRAINT `FK_53xvxooxtgbfppaln9leak72m` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_98i98ljbqxb2yp5bok9pu5wcc` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `application_status`
--

DROP TABLE IF EXISTS `application_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessor_feedback`
--

DROP TABLE IF EXISTS `assessor_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessor_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assessment_feedback` longtext,
  `assessment_value` varchar(255) DEFAULT NULL,
  `assessor_id` bigint(20) DEFAULT NULL,
  `response_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kpq74a1x07w0mof87ruig22ro` (`response_id`,`assessor_id`),
  KEY `FK_qdoh9x5tkw76s2h2oum4lqkor` (`assessor_id`),
  CONSTRAINT `FK_4fjfcjh3hufxg444uw4eo304d` FOREIGN KEY (`response_id`) REFERENCES `response` (`id`),
  CONSTRAINT `FK_qdoh9x5tkw76s2h2oum4lqkor` FOREIGN KEY (`assessor_id`) REFERENCES `process_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `competition`
--

DROP TABLE IF EXISTS `competition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `competition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assessment_end_date` date DEFAULT NULL,
  `assessment_start_date` date DEFAULT NULL,
  `description` longtext,
  `end_date` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `max_research_ratio` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost`
--

DROP TABLE IF EXISTS `cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cost` double DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `application_finance_id` bigint(20) DEFAULT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_14n47e1gx72ud7hj3t2yscu1v` (`application_finance_id`),
  KEY `FK_3ocl28vkv3coj1t5hmgixvl6` (`question_id`),
  CONSTRAINT `FK_14n47e1gx72ud7hj3t2yscu1v` FOREIGN KEY (`application_finance_id`) REFERENCES `application_finance` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_3ocl28vkv3coj1t5hmgixvl6` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_field`
--

DROP TABLE IF EXISTS `cost_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cost_value`
--

DROP TABLE IF EXISTS `cost_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cost_value` (
  `cost_id` bigint(20) NOT NULL DEFAULT '0',
  `cost_field_id` bigint(20) NOT NULL DEFAULT '0',
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cost_id`,`cost_field_id`),
  KEY `FK_h6lijwiwnsblqurwxjftvdn7n` (`cost_field_id`),
  CONSTRAINT `FK_cryaaiuibh4b0sqw3aqrkspmb` FOREIGN KEY (`cost_id`) REFERENCES `cost` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_h6lijwiwnsblqurwxjftvdn7n` FOREIGN KEY (`cost_field_id`) REFERENCES `cost_field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file_entry`
--

DROP TABLE IF EXISTS `file_entry`;
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

--
-- Table structure for table `form_input`
--

DROP TABLE IF EXISTS `form_input`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_input` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `word_count` int(11) DEFAULT NULL,
  `form_input_type_id` bigint(20) DEFAULT NULL,
  `competition_id` bigint(20) DEFAULT NULL,
  `included_in_application_summary` tinyint(1) NOT NULL DEFAULT '1',
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pvbo288244dfas1gd12t17pkv` (`form_input_type_id`),
  KEY `FK_hgdynqsaela2nuwm41nohmxg1` (`competition_id`),
  CONSTRAINT `FK_hgdynqsaela2nuwm41nohmxg1` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`),
  CONSTRAINT `FK_pvbo288244dfas1gd12t17pkv` FOREIGN KEY (`form_input_type_id`) REFERENCES `form_input_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `form_input_response`
--

DROP TABLE IF EXISTS `form_input_response`;
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

--
-- Table structure for table `form_input_type`
--

DROP TABLE IF EXISTS `form_input_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_input_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `form_input_validator`
--

DROP TABLE IF EXISTS `form_input_validator`;
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

--
-- Table structure for table `form_validator`
--

DROP TABLE IF EXISTS `form_validator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `form_validator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `clazz_name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invite`
--

DROP TABLE IF EXISTS `invite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `hash` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `application_id` bigint(20) DEFAULT NULL,
  `invite_organisation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_e5xbk2ld658t66m1rv60igb5s` (`application_id`,`email`),
  KEY `FK_skcmllljwagey78x7lmt2n00c` (`application_id`),
  KEY `FK_hexhehvongoy5cqgpem81xs86` (`invite_organisation_id`),
  CONSTRAINT `FK_hexhehvongoy5cqgpem81xs86` FOREIGN KEY (`invite_organisation_id`) REFERENCES `invite_organisation` (`id`),
  CONSTRAINT `FK_skcmllljwagey78x7lmt2n00c` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invite_organisation`
--

DROP TABLE IF EXISTS `invite_organisation`;
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

--
-- Table structure for table `organisation`
--

DROP TABLE IF EXISTS `organisation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `company_house_number` varchar(255) DEFAULT NULL,
  `organisation_size` varchar(255) DEFAULT NULL,
  `organisation_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_syoqdheljsd92k1vtdjfae31m` (`organisation_type_id`),
  CONSTRAINT `FK_syoqdheljsd92k1vtdjfae31m` FOREIGN KEY (`organisation_type_id`) REFERENCES `organisation_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organisation_address`
--

DROP TABLE IF EXISTS `organisation_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_type` varchar(255) DEFAULT NULL,
  `address_id` bigint(20) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g3y4ooi9akaq8e98efgmljigm` (`organisation_id`,`address_id`),
  KEY `FK_prle5vffxxpi4ibqymh6ic87x` (`address_id`),
  CONSTRAINT `FK_k8ipyjlxpsqfga85v2vhg0m0x` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_prle5vffxxpi4ibqymh6ic87x` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organisation_type`
--

DROP TABLE IF EXISTS `organisation_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organisation_type` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_organisation_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_eh40v8iivh39la2bmmr6h82u5` (`parent_organisation_type_id`),
  CONSTRAINT `FK_eh40v8iivh39la2bmmr6h82u5` FOREIGN KEY (`parent_organisation_type_id`) REFERENCES `organisation_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process`
--

DROP TABLE IF EXISTS `process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `event` varchar(255) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `process_type` varchar(31) NOT NULL,
  `process_role` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_emmws68ll8g6hlod8hvwk1t95` (`process_role`),
  CONSTRAINT `FK_emmws68ll8g6hlod8hvwk1t95` FOREIGN KEY (`process_role`) REFERENCES `process_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_outcome`
--

DROP TABLE IF EXISTS `process_outcome`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_outcome` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `outcome` varchar(255) DEFAULT NULL,
  `outcome_type` varchar(255) DEFAULT NULL,
  `process_id` bigint(20) DEFAULT NULL,
  `process_index` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rm72g2d5hsse93bn54jimfkbw` (`process_id`),
  CONSTRAINT `FK_rm72g2d5hsse93bn54jimfkbw` FOREIGN KEY (`process_id`) REFERENCES `process` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `process_role`
--

DROP TABLE IF EXISTS `process_role`;
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

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assessor_confirmation_question` varchar(255) DEFAULT NULL,
  `assign_enabled` bit(1) DEFAULT NULL,
  `description` longtext,
  `guidance_answer` longtext,
  `guidance_question` longtext,
  `mark_as_completed_enabled` bit(1) DEFAULT NULL,
  `multiple_statuses` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `needing_assessor_feedback` bit(1) NOT NULL,
  `needing_assessor_score` bit(1) NOT NULL,
  `priority` int(11) DEFAULT NULL,
  `question_number` varchar(255) DEFAULT NULL,
  `competition_id` bigint(20) DEFAULT NULL,
  `section_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_hgdynqsaela2nuwm41nohmxg0` (`competition_id`),
  KEY `FK_dyisjpgv8bmnrhy8t72lloed3` (`section_id`),
  CONSTRAINT `FK_dyisjpgv8bmnrhy8t72lloed3` FOREIGN KEY (`section_id`) REFERENCES `section` (`id`),
  CONSTRAINT `FK_hgdynqsaela2nuwm41nohmxg0` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_form_input`
--

DROP TABLE IF EXISTS `question_form_input`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_form_input` (
  `question_id` bigint(20) NOT NULL,
  `form_input_id` bigint(20) NOT NULL,
  `priority` int(11) NOT NULL,
  UNIQUE KEY `UK_8wu8lh9w3o8jwtvgrkgwn82ad` (`form_input_id`),
  KEY `FK_qwtjaey2uqlx08ccjpb02k7vl` (`question_id`),
  CONSTRAINT `FK_8wu8lh9w3o8jwtvgrkgwn82ad` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`),
  CONSTRAINT `FK_qwtjaey2uqlx08ccjpb02k7vl` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_status`
--

DROP TABLE IF EXISTS `question_status`;
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

--
-- Table structure for table `response`
--

DROP TABLE IF EXISTS `response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `response` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `update_date` datetime DEFAULT NULL,
  `application_id` bigint(20) DEFAULT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  `updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fxu0iyfgby8uecsrpkld59j7m` (`application_id`),
  KEY `FK_gtk1fk1uuqjy5gep9grawtl2r` (`question_id`),
  KEY `FK_rya6sd7m2suw65sdme40eldla` (`updated_by_id`),
  CONSTRAINT `FK_fxu0iyfgby8uecsrpkld59j7m` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_gtk1fk1uuqjy5gep9grawtl2r` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
  CONSTRAINT `FK_rya6sd7m2suw65sdme40eldla` FOREIGN KEY (`updated_by_id`) REFERENCES `process_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `section`
--

DROP TABLE IF EXISTS `section`;
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
  PRIMARY KEY (`id`),
  KEY `FK_525cnqb0rlfvo4ixk42jqvcxv` (`competition_id`),
  KEY `FK_pe8s9ptfql4li2ergosi21q9b` (`parent_section_id`),
  CONSTRAINT `FK_525cnqb0rlfvo4ixk42jqvcxv` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`),
  CONSTRAINT `FK_pe8s9ptfql4li2ergosi21q9b` FOREIGN KEY (`parent_section_id`) REFERENCES `section` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) DEFAULT NULL,
  `class_pk` bigint(20) DEFAULT NULL,
  `extra_info` longtext,
  `hash` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mtjr5jkw9dpbqhgx3mu1bomlt` (`hash`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `invite_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `uid` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_organisation`
--

DROP TABLE IF EXISTS `user_organisation`;
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

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  KEY `FK_it77eq964jhfqtu54081ebtio` (`role_id`),
  KEY `FK_apcc8lxk2xnug8377fatvbn04` (`user_id`),
  CONSTRAINT `FK_apcc8lxk2xnug8377fatvbn04` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_it77eq964jhfqtu54081ebtio` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
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

-- Dump completed on 2016-03-14 14:19:46
