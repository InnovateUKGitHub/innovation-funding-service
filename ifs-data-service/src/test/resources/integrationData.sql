-- MySQL dump 10.13  Distrib 5.5.43, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: ifs
-- ------------------------------------------------------
-- Server version	5.5.43-0ubuntu0.14.04.1

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
  `care_of` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `locality` varchar(255) DEFAULT NULL,
  `po_box` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`),
  KEY `FK_mdoygpekookkkntrvgy67jjlb` (`application_status_id`),
  KEY `FK_r6qpl12jw4qehsirycsa416ka` (`competition`),
  CONSTRAINT `FK_mdoygpekookkkntrvgy67jjlb` FOREIGN KEY (`application_status_id`) REFERENCES `application_status` (`id`),
  CONSTRAINT `FK_r6qpl12jw4qehsirycsa416ka` FOREIGN KEY (`competition`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
INSERT INTO `application` VALUES (1,51,'A novel solution to an old problem','2016-03-01',1,1);
INSERT INTO `application` VALUES (2,20,'Providing sustainable childcare','2015-11-01',2,1);
INSERT INTO `application` VALUES (3,10,'Mobile Phone Data for Logistics Analytics','2015-11-01',3,1);
INSERT INTO `application` VALUES (4,43,'Using natural gas to heat homes','2015-11-01',4,1);
INSERT INTO `application` VALUES (5,20,'A new innovative solution','2015-11-01',2,1);
INSERT INTO `application` VALUES (6,23,'Security for the Internet of Things','2015-11-01',2,1);
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`),
  KEY `FK_53xvxooxtgbfppaln9leak72m` (`application_id`),
  KEY `FK_98i98ljbqxb2yp5bok9pu5wcc` (`organisation_id`),
  CONSTRAINT `FK_53xvxooxtgbfppaln9leak72m` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_98i98ljbqxb2yp5bok9pu5wcc` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_finance`
--

LOCK TABLES `application_finance` WRITE;
/*!40000 ALTER TABLE `application_finance` DISABLE KEYS */;
INSERT INTO `application_finance` VALUES (1,1,3);
INSERT INTO `application_finance` VALUES (2,1,6);
INSERT INTO `application_finance` VALUES (3,1,4);
INSERT INTO `application_finance` VALUES (4,5,4);
INSERT INTO `application_finance` VALUES (5,2,3);
INSERT INTO `application_finance` VALUES (6,5,3);
INSERT INTO `application_finance` VALUES (7,5,6);
/*!40000 ALTER TABLE `application_finance` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_status`
--

LOCK TABLES `application_status` WRITE;
/*!40000 ALTER TABLE `application_status` DISABLE KEYS */;
INSERT INTO `application_status` VALUES (1,'created');
INSERT INTO `application_status` VALUES (2,'submitted');
INSERT INTO `application_status` VALUES (3,'approved');
INSERT INTO `application_status` VALUES (4,'rejected');
/*!40000 ALTER TABLE `application_status` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `assessor_feedback`
--

LOCK TABLES `assessor_feedback` WRITE;
/*!40000 ALTER TABLE `assessor_feedback` DISABLE KEYS */;
INSERT INTO `assessor_feedback` VALUES (5,'asdf','Yes',16,25);
INSERT INTO `assessor_feedback` VALUES (6,'qwer','3',16,21);
INSERT INTO `assessor_feedback` VALUES (7,'zxcv asdf qwer asdf','7',16,22);
INSERT INTO `assessor_feedback` VALUES (8,'','3',16,24);
INSERT INTO `assessor_feedback` VALUES (9,'asdfg','3',16,23);
INSERT INTO `assessor_feedback` VALUES (12,'','5',16,28);
INSERT INTO `assessor_feedback` VALUES (14,'asdf qwer asdf ','No',22,25);
INSERT INTO `assessor_feedback` VALUES (15,'weqr','10',22,21);
/*!40000 ALTER TABLE `assessor_feedback` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `competition`
--

LOCK TABLES `competition` WRITE;
/*!40000 ALTER TABLE `competition` DISABLE KEYS */;
INSERT INTO `competition` VALUES (1,'2015-12-31','2015-11-12','Innovate UK is to invest up to ?9 million in collaborative research and development to stimulate innovation in integrated transport solutions for local authorities. The aim of this competition is to meet user needs by connecting people and/or goods to transport products and services. New or improved systems will be tested in environment laboratories.','2016-03-16','Technology Inspired','2015-06-24');
/*!40000 ALTER TABLE `competition` ENABLE KEYS */;
UNLOCK TABLES;

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
  `application_finance_id` bigint(20) DEFAULT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_14n47e1gx72ud7hj3t2yscu1v` (`application_finance_id`),
  KEY `FK_3ocl28vkv3coj1t5hmgixvl6` (`question_id`),
  CONSTRAINT `FK_14n47e1gx72ud7hj3t2yscu1v` FOREIGN KEY (`application_finance_id`) REFERENCES `application_finance` (`id`),
  CONSTRAINT `FK_3ocl28vkv3coj1t5hmgixvl6` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cost`
--

LOCK TABLES `cost` WRITE;
/*!40000 ALTER TABLE `cost` DISABLE KEYS */;
INSERT INTO `cost` VALUES (1,NULL,'Working days per year',NULL,232,1,28);
INSERT INTO `cost` VALUES (2,50000,'','Manager',168,1,28);
INSERT INTO `cost` VALUES (4,30000,'','Engineer',696,1,28);
INSERT INTO `cost` VALUES (12,100,'','Powder',20,1,30);
INSERT INTO `cost` VALUES (13,150000,'specialist consultant','Mr Francis Bois',0,1,32);
INSERT INTO `cost` VALUES (15,NULL,'Working days per year',NULL,227,2,28);
INSERT INTO `cost` VALUES (16,NULL,'Working days per year',NULL,227,3,28);
INSERT INTO `cost` VALUES (17,45000,'','Manager',120,3,28);
INSERT INTO `cost` VALUES (18,140,'','Raw materials',120,3,30);
INSERT INTO `cost` VALUES (19,600,'','crucibles',6,1,30);
INSERT INTO `cost` VALUES (20,250,'','valves',12,1,30);
INSERT INTO `cost` VALUES (21,32000,'','Engineer',250,3,28);
INSERT INTO `cost` VALUES (22,28000,'','Technician',220,3,28);
INSERT INTO `cost` VALUES (23,50,'','components',30,3,30);
INSERT INTO `cost` VALUES (24,150,'','Tooling',7,3,30);
INSERT INTO `cost` VALUES (25,76800,'','Research engineer',132,2,28);
INSERT INTO `cost` VALUES (26,5000,'Machining of parts','East engineering',0,2,32);
INSERT INTO `cost` VALUES (27,400,'','Plates',5,2,30);
INSERT INTO `cost` VALUES (28,200,'','Powder',45,2,30);
INSERT INTO `cost` VALUES (29,NULL,'Working days per year',NULL,232,6,28);
INSERT INTO `cost` VALUES (30,50000,'','Manager',168,6,28);
INSERT INTO `cost` VALUES (31,30000,'','Engineer',696,6,28);
INSERT INTO `cost` VALUES (32,100,'','Powder',20,6,30);
INSERT INTO `cost` VALUES (33,150000,'specialist consultant','Mr Francis Bois',0,6,32);
INSERT INTO `cost` VALUES (34,NULL,'Working days per year',NULL,227,7,28);
INSERT INTO `cost` VALUES (35,NULL,'Working days per year',NULL,227,4,28);
INSERT INTO `cost` VALUES (36,45000,'','Manager',120,4,28);
INSERT INTO `cost` VALUES (37,140,'','Raw materials',120,4,30);
INSERT INTO `cost` VALUES (38,600,'','crucibles',6,6,30);
INSERT INTO `cost` VALUES (39,250,'','valves',12,6,30);
INSERT INTO `cost` VALUES (40,32000,'','Engineer',250,4,28);
INSERT INTO `cost` VALUES (41,28000,'','Technician',220,4,28);
INSERT INTO `cost` VALUES (42,50,'','components',30,4,30);
INSERT INTO `cost` VALUES (43,150,'','Tooling',7,4,30);
INSERT INTO `cost` VALUES (44,76800,'','Research engineer',132,7,28);
INSERT INTO `cost` VALUES (45,5000,'Machining of parts','East engineering',0,7,32);
INSERT INTO `cost` VALUES (46,400,'','Plates',5,7,30);
INSERT INTO `cost` VALUES (47,200,'','Powder',45,7,30);
INSERT INTO `cost` VALUES (48,0,'Grant Claim','',50,1,38);
INSERT INTO `cost` VALUES (49,0,'Grant Claim','',70,2,38);
INSERT INTO `cost` VALUES (50,0,'Grant Claim','',70,3,38);
INSERT INTO `cost` VALUES (51,0,'Accept Rate','Yes',23,1,29);
INSERT INTO `cost` VALUES (52,0,'Accept Rate','Yes',24,2,29);
INSERT INTO `cost` VALUES (53,0,'Accept Rate','Yes',25,3,29);
INSERT INTO `cost` VALUES (54,0,'Other Funding','Yes',NULL,1,35);
/*!40000 ALTER TABLE `cost` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `cost_field`
--

LOCK TABLES `cost_field` WRITE;
/*!40000 ALTER TABLE `cost_field` DISABLE KEYS */;
INSERT INTO `cost_field` VALUES (1,'country','String');
INSERT INTO `cost_field` VALUES (2,'existing','String');
INSERT INTO `cost_field` VALUES (3,'residual_value','BigDecimal');
INSERT INTO `cost_field` VALUES (4,'utilisation','Integer');
/*!40000 ALTER TABLE `cost_field` ENABLE KEYS */;
UNLOCK TABLES;

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
  CONSTRAINT `FK_cryaaiuibh4b0sqw3aqrkspmb` FOREIGN KEY (`cost_id`) REFERENCES `cost` (`id`),
  CONSTRAINT `FK_h6lijwiwnsblqurwxjftvdn7n` FOREIGN KEY (`cost_field_id`) REFERENCES `cost_field` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cost_value`
--

LOCK TABLES `cost_value` WRITE;
/*!40000 ALTER TABLE `cost_value` DISABLE KEYS */;
INSERT INTO `cost_value` VALUES (13,1,'France');
INSERT INTO `cost_value` VALUES (26,1,'UK');
INSERT INTO `cost_value` VALUES (33,1,'France');
INSERT INTO `cost_value` VALUES (45,1,'UK');
/*!40000 ALTER TABLE `cost_value` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `file_entry`
--

LOCK TABLES `file_entry` WRITE;
/*!40000 ALTER TABLE `file_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_entry` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_input`
--

LOCK TABLES `form_input` WRITE;
/*!40000 ALTER TABLE `form_input` DISABLE KEYS */;
INSERT INTO `form_input` VALUES (1,400,2,1,1,'1. What is the business opportunity that your project addresses?');
INSERT INTO `form_input` VALUES (2,400,2,1,1,'2. What is the size of the potential market for your project?');
INSERT INTO `form_input` VALUES (3,400,2,1,1,'3. How will you exploit and market your project?');
INSERT INTO `form_input` VALUES (4,400,2,1,1,'4. What economic, social and environmental benefits do you expect your project to deliver and when?');
INSERT INTO `form_input` VALUES (5,400,2,1,1,'5. What technical approach will you use and how will you manage your project?');
INSERT INTO `form_input` VALUES (6,400,2,1,1,'6. What is innovative about your project?');
INSERT INTO `form_input` VALUES (7,400,2,1,1,'7. What are the risks (technical, commercial and environmental) to your project\'s success? What is your risk management strategy?');
INSERT INTO `form_input` VALUES (8,400,2,1,1,'8. Does your project team have the skills, experience and facilities to deliver this project?');
INSERT INTO `form_input` VALUES (9,400,5,1,1,'Application details');
INSERT INTO `form_input` VALUES (11,400,2,1,1,'Project summary');
INSERT INTO `form_input` VALUES (12,400,2,1,1,'Public description');
INSERT INTO `form_input` VALUES (13,400,2,1,1,'How does your project align with the scope of this competition?');
INSERT INTO `form_input` VALUES (14,400,4,1,0,'Appendix');
INSERT INTO `form_input` VALUES (15,400,2,1,1,'9. What will your project cost?');
INSERT INTO `form_input` VALUES (16,400,2,1,1,'10. How does financial support from Innovate UK and its funding partners add value?');
INSERT INTO `form_input` VALUES (17,0,4,1,0,'Appendix');
INSERT INTO `form_input` VALUES (18,0,4,1,0,'Appendix');
INSERT INTO `form_input` VALUES (20,0,15,1,1,NULL);
INSERT INTO `form_input` VALUES (21,0,6,1,1,NULL);
INSERT INTO `form_input` VALUES (22,NULL,6,1,1,NULL);
INSERT INTO `form_input` VALUES (23,NULL,6,1,1,NULL);
INSERT INTO `form_input` VALUES (24,NULL,6,1,1,NULL);
INSERT INTO `form_input` VALUES (25,NULL,6,1,1,NULL);
INSERT INTO `form_input` VALUES (26,NULL,6,1,1,NULL);
INSERT INTO `form_input` VALUES (27,NULL,6,1,1,NULL);
INSERT INTO `form_input` VALUES (28,NULL,8,1,1,'Labour');
INSERT INTO `form_input` VALUES (29,NULL,9,1,1,'Overheads');
INSERT INTO `form_input` VALUES (30,NULL,10,1,1,'Materials');
INSERT INTO `form_input` VALUES (31,NULL,11,1,1,'Capital Usage');
INSERT INTO `form_input` VALUES (32,NULL,12,1,1,'Sub-contracting costs');
INSERT INTO `form_input` VALUES (33,NULL,13,1,1,'Travel and subsistence');
INSERT INTO `form_input` VALUES (34,NULL,14,1,1,'Other costs');
INSERT INTO `form_input` VALUES (35,NULL,17,1,1,'Other funding');
INSERT INTO `form_input` VALUES (36,NULL,16,1,1,NULL);
INSERT INTO `form_input` VALUES (38,0,7,1,1,'Please enter the grant % you wish to claim for this project');
/*!40000 ALTER TABLE `form_input` ENABLE KEYS */;
UNLOCK TABLES;

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
  CONSTRAINT `form_input_response_ibfk_1` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`),
  CONSTRAINT `FK_7901a8ft9tx1e02r82t84feaj` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`),
  CONSTRAINT `FK_e83s9n8p6d60v1on2730jf8m9` FOREIGN KEY (`updated_by_id`) REFERENCES `process_role` (`id`),
  CONSTRAINT `FK_fxu0iyfgby8uecsrpkld59j7n` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_input_response`
--

LOCK TABLES `form_input_response` WRITE;
/*!40000 ALTER TABLE `form_input_response` DISABLE KEYS */;
INSERT INTO `form_input_response` VALUES (1,'2015-09-18 10:33:27','Within the Industry one issue has caused progress in the field to be stifled.  Up until now any advancement has been made by working around this anomaly.  \r\n\r\nWe propose to tackle the situation head on and develop a tool that will circumvent the problem entirely allowing development to advance.\r\n',1,1,1,NULL);
INSERT INTO `form_input_response` VALUES (4,'2015-09-18 10:35:56','Wastage in our industry can be attributed in no small part to one issue.  To date businesses have been reluctant to tackle that problem and instead worked around it.  That has stifled progress.\r\n\r\nThe end result of our project will be a novel tool to manage the issue and substantially reduce the wastage caused by it.\r\n',12,1,1,NULL);
INSERT INTO `form_input_response` VALUES (5,'2015-09-18 10:35:16','The amount of money needed to complete this project is large.  \r\n\r\nThe academic phase will require the acquisition of several pieces of lab equipment and the employ of additional graduates to ensure the experiments run smoothly.\r\n\r\nThe Materials and use of the fabricators equipment will account for approximately some of the budget.\r\n\r\nThe time and expertise required across all 3 partners to complete each phase will amount to the lion share of the cost.\r\n',15,1,1,NULL);
INSERT INTO `form_input_response` VALUES (6,'2015-09-18 10:35:16','Although steps are under way to get this project off the ground, investment by Innovate UK will stimulate Industry leads to supply additional resource funding and use of their facilities as required to advance the progress of this project.  \r\n\r\nUK Investment will ensure that we are able to retain the onward manufacture internally instead of needing to create international deals taking work and profit out of the country.\r\n',16,1,1,NULL);
INSERT INTO `form_input_response` VALUES (7,'2015-09-18 10:33:27','The issue affects the entire field, the market value of which is significant, and accounts for a percentage of its annual overhead.  A tool to reduce that amount by a significant proportion would be taken up by the entire industry.  \r\n\r\nAt this time no other solutions are available or near market giving us the leading edge on market share.  \r\n\r\nManufacture and development could remain in the United Kingdom with an international export market.\r\n',2,1,1,NULL);
INSERT INTO `form_input_response` VALUES (8,'2015-09-18 10:33:27','The planned end result of the project would be a fully implementable tool that could be rolled out to the industry. \r\n\r\n Once a fully tested prototype can be demonstrated we can start to take orders and travel to international Industry leaders the exhibit the value of the tool.\r\n',3,1,1,NULL);
INSERT INTO `form_input_response` VALUES (9,'2015-09-18 10:33:27','The tool will give the consortium the leading edge on the technology as currently no one else is close to considering developing a solution.  \r\n\r\nThe Industry will be able to reduce wastage by a significant degree and pass those savings on to the consumer or reinvest into advancing the field.\r\n  \r\nInside 2 years it is estimated that a very large sum of money can be recouped by using our solution\r\n',4,1,1,NULL);
INSERT INTO `form_input_response` VALUES (10,'2015-09-18 10:26:33','Our Academic partner has developed a technique to identify the precise cause of the issue and have been able to demonstrate it at a small scale.  Taking this information they will scale up the experiment to a percentage of industrial scale to confirm.  \r\n\r\nWe will take this data and engineer a solution to isolate the problem we will then collude with our fabrication partner to design and prototype a tool to correct the obstacle. \r\n',5,1,1,NULL);
INSERT INTO `form_input_response` VALUES (11,'2015-09-18 10:26:33','The project is based on Manchester?s novel IP that, for the first time, allows SLM to be conducted without any need for anchors/supports. A search of the literature has established that the advances proposed in this project on which RAMP are based are not available via any commercially available process. In addition, the development and supply of tailored materials by Empire and a dedicated high temperature rig by Ludlow will involve further, critical degrees of innovation that will help to build a portfolio of IP to enhance commercial exploitation. NB. RAMP does not simply reduce stresses by working at higher temperatures; it involves a novel step based on manufacturing engineering and material science that completely eliminates stresses.',6,1,1,NULL);
INSERT INTO `form_input_response` VALUES (12,'2015-09-18 10:26:33','Three principal risks exist at this stage:\r\n1.        The academic scale up fails to replicate the smaller results either entirely or not to a satisfactory level. In this event the academic partner would begin a controlled scale back to identify if the method needs to be altered during scale up to replicate results.  In the event that the issue can only be identified in the small scale we would build that into our planning for the isolation phase\r\n2.      During the phase to engineer a technique to isolate the issue we anticipate that there are inherent safety issues.  We have consulted an independent firm to ensure that suitable equipment and measures have been put in place to minimise the risk to staff.\r\n3.    Although we are confident that our fabrication partner is the best option for the prototype solution there is an indication that a particular piece of heavy machinery, currently unavailable in the UK will be required.  Our Fabrication partner has already investigated options for accessing this equipment in the Far East and an appropriate budget has been set aside to be used for this purpose if required.\r\n',7,1,1,NULL);
INSERT INTO `form_input_response` VALUES (13,'2015-09-18 10:26:33','The team consists of:\r\nUs - We have been supplying engineering solutions to the industry for a number of years and have been principal deliverers for various advancements within the field.  We are a suitably sized team capable of developing flexible solutions to a wide variety of clients internationally.\r\n\r\nAcademic ? the principal investigator and his team have been studying the issue for a number of years and have qualifications across the spectrum that apply to the field.  Together they have already identified several efficiencies that can be made within the industry.\r\n\r\nThe Fabricators ? Are a leading team within complex engineering fabrication.  They have work on multiple high budget engineering solutions and consist of a team of people with many years? experience in a range of industries\r\n',8,1,1,NULL);
INSERT INTO `form_input_response` VALUES (15,'2015-09-18 10:35:56','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',11,1,1,NULL);
INSERT INTO `form_input_response` VALUES (16,'2015-08-08 00:00:00','file.doc',14,1,1,NULL);
INSERT INTO `form_input_response` VALUES (17,'2015-08-08 00:00:00','file.pdf',17,1,1,NULL);
INSERT INTO `form_input_response` VALUES (18,'2015-09-18 10:30:39','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',13,1,1,NULL);
INSERT INTO `form_input_response` VALUES (19,'2015-10-02 17:03:28','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',11,10,5,NULL);
INSERT INTO `form_input_response` VALUES (20,'2015-10-02 17:03:42','Wastage in our industry can be attributed in no small part to one issue.  To date businesses have been reluctant to tackle that problem and instead worked around it.  That has stifled progress.\r\n\r\nThe end result of our project will be a novel tool to manage the issue and substantially reduce the wastage caused by it.\r\n',12,10,5,NULL);
INSERT INTO `form_input_response` VALUES (21,'2015-10-02 17:04:24','Within the Industry one issue has caused progress in the field to be stifled.  Up until now any advancement has been made by working around this anomaly.  \r\n\r\nWe propose to tackle the situation head on and develop a tool that will circumvent the problem entirely allowing development to advance.\r\n',1,10,5,NULL);
INSERT INTO `form_input_response` VALUES (22,'2015-10-02 17:05:51','The issue affects the entire field, the market value of which is significant, and accounts for a percentage of its annual overhead.  A tool to reduce that amount by a significant proportion would be taken up by the entire industry.  \r\n\r\nAt this time no other solutions are available or near market giving us the leading edge on market share.  \r\n\r\nManufacture and development could remain in the United Kingdom with an international export market.\r\n',2,10,5,NULL);
INSERT INTO `form_input_response` VALUES (23,'2015-10-02 17:06:06','The planned end result of the project would be a fully implementable tool that could be rolled out to the industry. \r\n\r\n Once a fully tested prototype can be demonstrated we can start to take orders and travel to international Industry leaders the exhibit the value of the tool.',3,10,5,NULL);
INSERT INTO `form_input_response` VALUES (24,'2015-10-02 17:06:20','The tool will give the consortium the leading edge on the technology as currently no one else is close to considering developing a solution.  \r\n\r\nThe Industry will be able to reduce wastage by a significant degree and pass those savings on to the consumer or reinvest into advancing the field.\r\n  \r\nInside 2 years it is estimated that a very large sum of money can be recouped by using our solution\r\n',4,10,5,NULL);
INSERT INTO `form_input_response` VALUES (25,'2015-10-02 17:04:05','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.',13,10,5,NULL);
INSERT INTO `form_input_response` VALUES (26,'2015-10-02 17:06:42','Our Academic partner has developed a technique to identify the precise cause of the issue and have been able to demonstrate it at a small scale.  Taking this information they will scale up the experiment to a percentage of industrial scale to confirm.  \r\n\r\nWe will take this data and engineer a solution to isolate the problem we will then collude with our fabrication partner to design and prototype a tool to correct the obstacle. \r\n',5,10,5,NULL);
INSERT INTO `form_input_response` VALUES (27,'2015-10-02 17:06:54','The project is based on Manchester?s novel IP that, for the first time, allows SLM to be conducted without any need for anchors/supports. A search of the literature has established that the advances proposed in this project on which RAMP are based are not available via any commercially available process. In addition, the development and supply of tailored materials by Empire and a dedicated high temperature rig by Ludlow will involve further, critical degrees of innovation that will help to build a portfolio of IP to enhance commercial exploitation. NB. RAMP does not simply reduce stresses by working at higher temperatures; it involves a novel step based on manufacturing engineering and material science that completely eliminates stresses.',6,10,5,NULL);
INSERT INTO `form_input_response` VALUES (28,'2015-10-02 17:07:17','Three principal risks exist at this stage:\r\n1.        The academic scale up fails to replicate the smaller results either entirely or not to a satisfactory level. In this event the academic partner would begin a controlled scale back to identify if the method needs to be altered during scale up to replicate results.  In the event that the issue can only be identified in the small scale we would build that into our planning for the isolation phase\r\n2.      During the phase to engineer a technique to isolate the issue we anticipate that there are inherent safety issues.  We have consulted an independent firm to ensure that suitable equipment and measures have been put in place to minimise the risk to staff.\r\n3.    Although we are confident that our fabrication partner is the best option for the prototype solution there is an indication that a particular piece of heavy machinery, currently unavailable in the UK will be required.  Our Fabrication partner has already investigated options for accessing this equipment in the Far East and an appropriate budget has been set aside to be used for this purpose if required.\r\n',7,10,5,NULL);
INSERT INTO `form_input_response` VALUES (29,'2015-10-02 17:07:31','The team consists of:\r\nUs - We have been supplying engineering solutions to the industry for a number of years and have been principal deliverers for various advancements within the field.  We are a suitably sized team capable of developing flexible solutions to a wide variety of clients internationally.\r\n\r\nAcademic ? the principal investigator and his team have been studying the issue for a number of years and have qualifications across the spectrum that apply to the field.  Together they have already identified several efficiencies that can be made within the industry.\r\n\r\nThe Fabricators ? Are a leading team within complex engineering fabrication.  They have work on multiple high budget engineering solutions and consist of a team of people with many years? experience in a range of industries\r\n',8,10,5,NULL);
INSERT INTO `form_input_response` VALUES (30,'2015-10-02 17:07:50','The amount of money needed to complete this project is large.  \r\n\r\nThe academic phase will require the acquisition of several pieces of lab equipment and the employ of additional graduates to ensure the experiments run smoothly.\r\n\r\nThe Materials and use of the fabricators equipment will account for approximately some of the budget.\r\n\r\nThe time and expertise required across all 3 partners to complete each phase will amount to the lion share of the cost.\r\n',15,10,5,NULL);
INSERT INTO `form_input_response` VALUES (31,'2015-10-02 17:08:03','Although steps are under way to get this project off the ground, investment by Innovate UK will stimulate Industry leads to supply additional resource funding and use of their facilities as required to advance the progress of this project.  \r\n\r\nUK Investment will ensure that we are able to retain the onward manufacture internally instead of needing to create international deals taking work and profit out of the country.\r\n',16,10,5,NULL);
INSERT INTO `form_input_response` VALUES (32,'2015-10-02 17:08:03','score',14,10,5,NULL);
INSERT INTO `form_input_response` VALUES (33,'2015-10-02 17:08:03','score',17,10,5,NULL);
INSERT INTO `form_input_response` VALUES (34,'2015-10-02 17:08:03','score',18,10,5,NULL);
INSERT INTO `form_input_response` VALUES (35,'2015-10-08 21:05:45','2',38,1,1,NULL);
/*!40000 ALTER TABLE `form_input_response` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_input_type`
--

LOCK TABLES `form_input_type` WRITE;
/*!40000 ALTER TABLE `form_input_type` DISABLE KEYS */;
INSERT INTO `form_input_type` VALUES (1,'textinput');
INSERT INTO `form_input_type` VALUES (2,'textarea');
INSERT INTO `form_input_type` VALUES (3,'date');
INSERT INTO `form_input_type` VALUES (4,'fileupload');
INSERT INTO `form_input_type` VALUES (5,'application_details');
INSERT INTO `form_input_type` VALUES (6,'empty');
INSERT INTO `form_input_type` VALUES (7,'finance');
INSERT INTO `form_input_type` VALUES (8,'labour');
INSERT INTO `form_input_type` VALUES (9,'overheads');
INSERT INTO `form_input_type` VALUES (10,'materials');
INSERT INTO `form_input_type` VALUES (11,'capital_usage');
INSERT INTO `form_input_type` VALUES (12,'subcontracting_costs');
INSERT INTO `form_input_type` VALUES (13,'travel');
INSERT INTO `form_input_type` VALUES (14,'other_costs');
INSERT INTO `form_input_type` VALUES (15,'your_finance');
INSERT INTO `form_input_type` VALUES (16,'financial_summary');
INSERT INTO `form_input_type` VALUES (17,'other_funding');
INSERT INTO `form_input_type` VALUES (18,'percentage');
/*!40000 ALTER TABLE `form_input_type` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_input_validator`
--

LOCK TABLES `form_input_validator` WRITE;
/*!40000 ALTER TABLE `form_input_validator` DISABLE KEYS */;
INSERT INTO `form_input_validator` VALUES (1,2);
INSERT INTO `form_input_validator` VALUES (2,2);
INSERT INTO `form_input_validator` VALUES (3,2);
INSERT INTO `form_input_validator` VALUES (4,2);
INSERT INTO `form_input_validator` VALUES (5,2);
INSERT INTO `form_input_validator` VALUES (6,2);
INSERT INTO `form_input_validator` VALUES (7,2);
INSERT INTO `form_input_validator` VALUES (8,2);
INSERT INTO `form_input_validator` VALUES (11,2);
INSERT INTO `form_input_validator` VALUES (12,2);
INSERT INTO `form_input_validator` VALUES (13,2);
INSERT INTO `form_input_validator` VALUES (15,2);
INSERT INTO `form_input_validator` VALUES (16,2);
INSERT INTO `form_input_validator` VALUES (1,3);
INSERT INTO `form_input_validator` VALUES (2,3);
INSERT INTO `form_input_validator` VALUES (3,3);
INSERT INTO `form_input_validator` VALUES (4,3);
INSERT INTO `form_input_validator` VALUES (5,3);
INSERT INTO `form_input_validator` VALUES (6,3);
INSERT INTO `form_input_validator` VALUES (7,3);
INSERT INTO `form_input_validator` VALUES (8,3);
INSERT INTO `form_input_validator` VALUES (11,3);
INSERT INTO `form_input_validator` VALUES (12,3);
INSERT INTO `form_input_validator` VALUES (13,3);
INSERT INTO `form_input_validator` VALUES (15,3);
INSERT INTO `form_input_validator` VALUES (16,3);
/*!40000 ALTER TABLE `form_input_validator` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `form_validator`
--

LOCK TABLES `form_validator` WRITE;
/*!40000 ALTER TABLE `form_validator` DISABLE KEYS */;
INSERT INTO `form_validator` VALUES (1,'com.worth.ifs.validator.EmailValidator','EmailValidator');
INSERT INTO `form_validator` VALUES (2,'com.worth.ifs.validator.NotEmptyValidator','NotEmptyValidator');
INSERT INTO `form_validator` VALUES (3,'com.worth.ifs.validator.WordCountValidator','WordCountValidator');
/*!40000 ALTER TABLE `form_validator` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organisation`
--

LOCK TABLES `organisation` WRITE;
/*!40000 ALTER TABLE `organisation` DISABLE KEYS */;
INSERT INTO `organisation` VALUES (1,'Nomensa',NULL,NULL);
INSERT INTO `organisation` VALUES (2,'Worth Internet Systems',NULL,NULL);
INSERT INTO `organisation` VALUES (3,'Empire Ltd',NULL,NULL);
INSERT INTO `organisation` VALUES (4,'Ludlow',NULL,NULL);
INSERT INTO `organisation` VALUES (5,'Manchester University',NULL,NULL);
INSERT INTO `organisation` VALUES (6,'EGGS',NULL,NULL);
INSERT INTO `organisation` VALUES (7,'AA Ltd',NULL,NULL);
/*!40000 ALTER TABLE `organisation` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organisation_address`
--

LOCK TABLES `organisation_address` WRITE;
/*!40000 ALTER TABLE `organisation_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `organisation_address` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `process`
--

LOCK TABLES `process` WRITE;
/*!40000 ALTER TABLE `process` DISABLE KEYS */;
INSERT INTO `process` VALUES (1,NULL,'recommend','2015-10-01 11:42:40',NULL,'assessed','Assessment',7);
INSERT INTO `process` VALUES (2,NULL,'','2015-09-22 14:34:16',NULL,'pending','Assessment',8);
INSERT INTO `process` VALUES (3,NULL,'recommend','2015-10-08 15:16:19',NULL,'assessed','Assessment',16);
INSERT INTO `process` VALUES (4,NULL,'','2015-10-01 11:42:54',NULL,'pending','Assessment',17);
INSERT INTO `process` VALUES (5,NULL,'','2015-10-07 11:22:33',NULL,'pending','Assessment',20);
INSERT INTO `process` VALUES (6,NULL,'','2015-10-07 11:22:33',NULL,'pending','Assessment',21);
INSERT INTO `process` VALUES (7,NULL,'recommend','2015-10-08 16:31:00',NULL,'assessed','Assessment',22);
INSERT INTO `process` VALUES (8,NULL,'','2015-10-07 11:22:33',NULL,'pending','Assessment',23);
/*!40000 ALTER TABLE `process` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `process_outcome`
--

LOCK TABLES `process_outcome` WRITE;
/*!40000 ALTER TABLE `process_outcome` DISABLE KEYS */;
INSERT INTO `process_outcome` VALUES (1,NULL,'hey','YES','recommend',1,0);
INSERT INTO `process_outcome` VALUES (2,NULL,NULL,'YES','recommend',2,0);
INSERT INTO `process_outcome` VALUES (3,NULL,NULL,'YES','recommend',3,0);
INSERT INTO `process_outcome` VALUES (4,NULL,NULL,'YES','recommend',7,0);
/*!40000 ALTER TABLE `process_outcome` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `process_role`
--

LOCK TABLES `process_role` WRITE;
/*!40000 ALTER TABLE `process_role` DISABLE KEYS */;
INSERT INTO `process_role` VALUES (1,1,3,1,1);
INSERT INTO `process_role` VALUES (2,2,3,1,1);
INSERT INTO `process_role` VALUES (3,3,3,1,1);
INSERT INTO `process_role` VALUES (4,4,3,1,1);
INSERT INTO `process_role` VALUES (5,1,4,2,2);
INSERT INTO `process_role` VALUES (6,4,4,2,2);
INSERT INTO `process_role` VALUES (7,3,2,3,3);
INSERT INTO `process_role` VALUES (8,4,3,3,3);
INSERT INTO `process_role` VALUES (9,1,6,2,8);
INSERT INTO `process_role` VALUES (10,5,3,1,1);
INSERT INTO `process_role` VALUES (11,6,4,1,2);
INSERT INTO `process_role` VALUES (12,5,4,2,2);
INSERT INTO `process_role` VALUES (13,5,6,2,8);
INSERT INTO `process_role` VALUES (15,2,2,3,3);
INSERT INTO `process_role` VALUES (16,5,2,3,3);
INSERT INTO `process_role` VALUES (17,6,2,3,3);
INSERT INTO `process_role` VALUES (18,1,2,3,9);
INSERT INTO `process_role` VALUES (19,2,2,3,9);
INSERT INTO `process_role` VALUES (20,3,2,3,9);
INSERT INTO `process_role` VALUES (21,4,2,3,9);
INSERT INTO `process_role` VALUES (22,5,2,3,9);
INSERT INTO `process_role` VALUES (23,6,2,3,9);
/*!40000 ALTER TABLE `process_role` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,NULL,'',NULL,'<p>You should describe:</p><ul class=\"list-bullet\">         <li>the business opportunity you have identified and how you plan to take advantage of it</li><li>the customer needs you have identified and how your project will meet them</li><li>the challenges you expect to face and how you will overcome them</li></ul>','What should I include in the business opportunity section?','','\0','What is the business opportunity that your project addresses?','Business opportunity','','',5,'1',1,2);
INSERT INTO `question` VALUES (2,NULL,'',NULL,'<p>Describe the size of the potential market for your project, including:</p><ul class=\"list-bullet\">         <li>details of your target market, for instance, how competitive and profitable it is</li><li>the current size of the market, with actual and predicted growth rates</li><li>the market share you expect to achieve and the reasons for this estimate</li><li>the wider economic value you expect your project to add to the UK and/or the EEA (European Economic Area)</li></ul><p>Tell us what return on investment you expect your project to achieve. You should base this estimate on relevant industry data and tell us how you have calculated this.</p><p>If you are targeting an undeveloped market, you should also:</p><ul class=\"list-bullet\">         <li>describe how you plan to access this market</li><li>estimate its potential size</li><li>explain how you will explore its potential</li></ul>','What should I include in the market opportunity section?','','\0','What is the size of the potential market for your project?','Potential market','','',6,'2',1,2);
INSERT INTO `question` VALUES (3,NULL,'',NULL,'<p>Describe the potential outputs of the project, such as:</p><ul class=\"list-bullet\">         <li>products or services</li><li>processes</li><li>applications</li></ul><p>Describe how you will exploit these outputs, such as:</p><ul class=\"list-bullet\">         <li>the route to market</li><li>protection of intellectual property rights</li><li>reconfiguration of your organisation\'s value system</li><li>changes to business models and processes</li><li>any other methods of exploitation and protection</li></ul> ','What should I include in the project exploitation section?','','\0','How will you exploit and market your project?','Project exploitation','','',7,'3',1,2);
INSERT INTO `question` VALUES (4,NULL,'',NULL,'<p>Describe all the benefits you expect your project to deliver, including:</p><strong>Economic</strong> ? this is the real impact the project will have on its economic environment. This is not traditional corporate accounting profit and can include cost avoidance. You should identify and quantify any expected benefits to:</p><ul class=\"list-bullet\">         <li>users (intermediaries and end users)</li><li>suppliers</li><li>broader industrial markets</li><li>the UK economy</li></ul><p><strong>Social</strong> - quantify any expected social impacts, either positive or negative, on, for example:</p><ul class=\"list-bullet\">         <li>quality of life</li><li>social inclusion/exclusion</li><li>education</li><li>public empowerment</li><li>health and safety</li><li>regulation</li><li>diversity</li><li>government priorities</li></ul><p><strong>Environmental</strong> ? show how your project will benefit the environment or have low impact. For example, this could include:<p><ul class=\"list-bullet\">         <li>careful management of energy consumption</li><li>reductions in carbon emissions</li><li>reducing manufacturing and materials waste</li></li>rendering waste less toxic before disposing of it in a safe and legal manner</li><li>re-manufacturing (cradle to cradle)</li></ul>','What should I include in the benefits section?','','\0','What economic, social and environmental benefits do you expect your project to deliver and when?','Economic benefit','','',8,'4',1,2);
INSERT INTO `question` VALUES (5,NULL,'','Describe the areas of work and your objectives. List all resource and management needs. Provide an overview of your technical approach.','<p>You should:</p><ul class=\"list-bullet\">         <li>describe your technical approach including the main objectives of the work</li><li>explain how and why your approach is appropriate</li><li>tell us how you will ensure that the innovative steps in your project are achievable</li><li>describe rival technologies and alternative R&D strategies</li><li>explain why your proposed approach will offer a better outcome</li></ul>','What should I include in the technical approach section?','','\0','What technical approach will you use and how will you manage your project?','Technical approach','','',9,'5',1,2);
INSERT INTO `question` VALUES (6,NULL,'','Explain how your project is innovative in both a commercial and technical sense.','<p>You should show how your project will:</p><ul class=\"list-bullet\">         <li>push boundaries beyond current leading-edge science and technology</li><li>apply existing technologies in new areas</li></ul><p>Explain the novelty of the research in an industrial and/or academic context.</p><p>You should provide evidence that your proposed work is innovative. This could include patent search results, competitor analyses or literature surveys. If relevant, you should also outline your own intellectual property rights.</p>','What should I include in the project innovation section?','','\0','What is innovative about your project?','Innovation','','',10,'6',1,2);
INSERT INTO `question` VALUES (7,NULL,'','We recognise that many of the projects we fund are risky. This is why we need to be sure that you have an adequate plan for managing this risk.','<p>Please describe your plans for limiting and managing risk. You need to:</p><ul class=\"list-bullet\">         <li>identify the project\'s main risks and uncertainties</li><li>detail specific technical, commercial, managerial and environmental risks</li><li>list any other uncertainties such as ethical issues associated with the project</li><li>provide a detailed risk analysis</li><li>rate the main risks as high/medium/low</li><li>show how you\'ll limit the main risks</li><li>identify the project management tools and mechanisms you\'ll use to minimise operational risk</li><li>include arrangements for managing the project team and its partners</li></ul>','What should I include in the project risks section?','','\0','What are the risks (technical, commercial and environmental) to your project\'s success? What is your risk management strategy?','Risks','','',11,'7',1,2);
INSERT INTO `question` VALUES (8,NULL,'','Describe your capability to develop and exploit this technology. Include details of your team\'s track record in managing research and development projects.','<p>You should show your project team:</p><ul class=\"list-bullet\">         <li>has the right mix of skills and experience to complete the project</li><li>has clear objectives</li><li>how it would have been formed even without Innovate UK investment</li></ul><p>If you are part of a consortium, describe the benefits of the collaboration. For example, increased knowledge transfer.</p>','What should I include in the project skills section?','','\0','Does your project team have the skills, experience and facilities to deliver this project?','Project team','','',12,'8',1,2);
INSERT INTO `question` VALUES (9,NULL,'\0','Enter the full title of the project',NULL,NULL,'','\0','Application details','Application details','\0','\0',1,NULL,1,1);
INSERT INTO `question` VALUES (11,NULL,'','Please provide a short summary of your project. Make sure you include what is innovative about it.','<p>We will not score this summary, but it will give the assessors a useful introduction to your project. It should provide a clear overview of the whole project, including:</p> <ul class=\"list-bullet\">         <li>your vision for the project</li><li>key objectives</li><li>main areas of focus</li><li>details of how it is innovative</li></ul>','What should I include in the project summary?','','\0','Project summary','Project summary\n','\0','\0',2,NULL,1,1);
INSERT INTO `question` VALUES (12,NULL,'','Please provide a brief description of your project. If your application is successful, we will publish this description. This question is mandatory but we will not assess this content as part of your application.','<p>Innovate UK publishes information about projects we have funded. This is in line with government practice on openness and transparency of public-funded activities.</p><p>Describe your project in a way that will be easy for a non-specialist to understand. Don\'t include any information that is confidential, for example, intellectual property or patent details.</p> ','What should I include in the project public description?','','\0','Public description','Public description\n','\0','\0',3,NULL,1,1);
INSERT INTO `question` VALUES (13,'Is this application in scope?','','If your application doesn\'t align with the scope, we will reject it.','<p>It is important that you read the following guidance.</p><p>To show how your project aligns with the scope of this competition, you need to:</p><ul class=\"list-bullet\">         <li>read the competition brief in full</li><li>understand the background, challenge and scope of the competition</li><li>address the research objectives in your application</li><li>match your project\'s objectives and activities to these</li></ul> <p>Once you have submitted your application, you should not change this section unless:</p><ul class=\"list-bullet\">         <li>we ask you to provide more information</li><li>we ask you to make it clearer</li></ul> ','What should I include in the project scope?','','\0','How does your project align with the scope of this competition?','Scope','','\0',4,NULL,1,1);
INSERT INTO `question` VALUES (15,NULL,'','Tell us the toal costs of the project and how much funding you need from Innovate UK. Please provide details of your expected project costs along with any supporting information. Please justify any large expenditure in your project.','<p>You must:</p><ul class=\"list-bullet\">         <li>show how your budget is realistic for the scale and complexity of the project</li><li>make sure the funding you need from Innovate UK is within the limit set by this competition</li><li>justify any significant costs in the project, such as subcontractors</li><li>show how much funding there will be from other sources</li><li>provide a realistic budget breakdown</li><li>describe and justify individual work packages</li></ul><p>Find out which project costs are eligible: https://interact.innovateuk.org/-/project-costs</p><p>If your project spans more than one type of research category, you must break down the costs as separate \'work packages\'. For example, industrial research or experimental development. </p><p>You can find more information in the guidance section of your website:\n https://interact.innovateuk.org/-/funding-rules</p>','What should I include in the project cost section?','','\0','What will your project cost?','Funding','','',13,'9',1,2);
INSERT INTO `question` VALUES (16,NULL,'',' ','Justify why you\'re unable to fund the project yourself from commercial resources. Explain the difference this funding will make to your project. For example, will it lower the risk for you or speed up the process of getting your product to market? Tell us why this will benefit the UK.','What should I include in the financial support from Innovate UK section?','','\0','How does financial support from Innovate UK and its funding partners add value?','Adding value','','',14,'10',1,2);
INSERT INTO `question` VALUES (20,NULL,'','Only your organisation can see this level of detail. All members of your organisation can acesss and edit your finances We recommend assigning completion of your finances to one member of your team','','','\0','\0','Provide your organisation\'s finances',NULL,'\0','\0',15,NULL,1,7);
INSERT INTO `question` VALUES (21,NULL,'','<p>You may claim the labour costs of all individuals you have working on your project.</p> <p> If your application is awarded funding, you will need to account for all your labour costs as they occur. For example, you should keep timesheets and payroll records. These should show the actual hours worked by individuals and paid by the organisation.</p>','<p>You can include the following labour costs, based upon your PAYE records:</p> <ul class=\"list-bullet\">         <li>gross salary</li><li>National Insurance</li><li>company pension contribution</li><li>life insurance</li><li>other non-discretionary package costs.</li>     </ul><p>You can\'t include:</p><ul class=\"list-bullet\">         <li>discretionary bonuses</li><li>performance related payments of any kind</li></ul> <p>We base the total number of working days per year on full time days less standard holiday allowance. You should not include:</p><ul class=\"list-bullet\">         <li>sick days</li><li>waiting time</li><li>training days</li><li>non-productive time</li></ul> <p>On the finance form, list the total days worked by all categories of staff on your project. Describe their role.</p><p>We will review the total amount of time and cost before we approve your application. The terms and conditions of the grant include compliance with these points.','Labour costs guidance','\0','',NULL,NULL,'\0','\0',1,NULL,1,9);
INSERT INTO `question` VALUES (22,NULL,'','<p>Overheads are incremental indirect expenses incurred as a result of delivering the project. They are eligible for project funding.</p>','We will review and test your calculation. This is so that we can make sure that the items included are eligible and reasonable</p><p>Please read our guide to claiming Overheads for further information.</p>','Overheads guidance','\0','',NULL,NULL,'\0','\0',1,NULL,1,10);
INSERT INTO `question` VALUES (23,NULL,'','<p>You can claim the costs of materials used on your project providing:</p><ul class=\"list-bullet\">         <li>they are not already purchased or included in the overheads</li><li>they won\'t have a residual/resale value at the end of your project. If they do, you can claim the costs minus this value.</li></ul><p>Please refer to our guide to project costs for further information.</p>','If you are using materials supplied by associated companies or sub contracted from other consortium members then you are required to exclude the profit element of the value placed on that material - the materials should be charged at cost.\n\nSoftware that you have purchased specifically for use during your project should be included in materials.\n\nHowever if you already own software which will be used in the project, or it is provided for usage within your consortium by a consortium member, only additional costs incurred & paid between the start and end of your project will be eligible. Examples of costs that may be eligible are those related to the preparation of disks, manuals, installation, training or customisation.','Materials costs guidance','\0','',NULL,NULL,'\0','\0',1,NULL,1,11);
INSERT INTO `question` VALUES (24,NULL,'','<p>Capital usage refers to an asset that you will use in your project. The asset will have a useful life of more than one year, be stand-alone, distinct and moveable.</p>','<p>You should provide details of capital equipment and tools you will buy for, or use on, your project.</p><p>You will need to calculate a ?usage? value for each item. You can do this by deducting its expected value at the end of your project from its original price. If you owned the equipment before you started the project, you should use its Net Present Value.</p><p>This value is then multiplied by the percentage that your project will be utilising the equipment. This final value represents the eligible cost to your project.</p>','Capital usage guidance','\0','',NULL,NULL,'\0','\0',1,NULL,1,12);
INSERT INTO `question` VALUES (25,NULL,'','<p>Subcontract costs relate to work carried out by third party organisations. These organisations are not part of your project or collaboration. You may subcontract work if you don\'t have the expertise in your consortium. You can also subcontract if it is cheaper than developing your skills in-house.</p>','Subcontract services supplied by associated companies should exclude any profit element and be charged at cost.\n\nYou should name the subcontractor (where known) and describe what the subcontractor will be doing and where the work will be undertaken. We will look at the size of this contribution when assessing eligibility and level of support.\n','Subcontracting costs guidance','\0','',NULL,NULL,'\0','\0',1,NULL,1,13);
INSERT INTO `question` VALUES (26,NULL,'','<p>You should include travel and subsistence costs that relate only to this project. </p>',NULL,NULL,'\0','',NULL,NULL,'\0','\0',1,NULL,1,14);
INSERT INTO `question` VALUES (27,NULL,'','<p>You can use this section to detail costs that do not fit under the other cost headings. You should describe each type of cost and explain why you have included it.</p>','<p>Examples of other costs include:</p><p><strong>Training costs</strong> ? these costs are eligible for support if they relate to your project. We may support management training for your project but will not support ongoing training.</p><p><strong>Preparation of technical reports</strong> ? for example, if the main aim of your project is standards support or technology transfer. You should show how this is more than you would produce through good project management.</p><p><strong>Market assessment</strong> ? we may support of market assessments studies. The study will need to help us understand how your project is a good match for your target market. It could also be eligible if it helps commercialise your product</p></p><strong>Licensing in new technologies</strong> ? if new technology makes up a large part of your project, we will expect you to develop that technology. For instance, if the value of the technology is more than ?100,000. </p><p><strong>Patent filing costs for NEW IP generated by your project</strong> - these are eligible for SMEs up to a limit of ?7,500 per partner. You should not include legal costs relating to the filing or trademark related expenditure.</p><p>Regulatory compliance costs are eligible if necessary to carry out your project.</p>','Other costs guidance','\0','',NULL,NULL,'\0','\0',1,NULL,1,15);
INSERT INTO `question` VALUES (28,NULL,'',NULL,NULL,NULL,'','','Labour',NULL,'\0','\0',2,NULL,1,9);
INSERT INTO `question` VALUES (29,NULL,'','A common way of estimating overheads on Innovate UK projects is to use a flat rate of 20% of labour costs. This is the default rate. If you want to use a different rate, please enter it below and we will assess your overheads on this basis.  If your project is successful, you will need to justify this rate to our finance team. Two methods of declaring overheads are available:',NULL,NULL,'','','Overheads',NULL,'\0','\0',2,NULL,1,10);
INSERT INTO `question` VALUES (30,NULL,'','Please provide a breakdown of the materials you expect to use during the project',NULL,NULL,'','','Materials',NULL,'\0','\0',2,NULL,1,11);
INSERT INTO `question` VALUES (31,NULL,'','Please provide a breakdown of the capital items you will buy and/or use for the project.',NULL,NULL,'','','Capital Usage',NULL,'\0','\0',2,NULL,1,12);
INSERT INTO `question` VALUES (32,NULL,'','Please provide details of any work that you expect to subcontract for your project.',NULL,NULL,'','','Sub-contracting costs',NULL,'\0','\0',2,NULL,1,13);
INSERT INTO `question` VALUES (33,NULL,'',NULL,NULL,NULL,'','','Travel and subsistence',NULL,'\0','\0',2,NULL,1,14);
INSERT INTO `question` VALUES (34,NULL,'','Please note that legal or project audit and accountancy fees are not eligible and should not be included as an \'other cost\'.Patent filing costs of NEW IP relating to the project are limited to ?5,000 for SME applicants only.\n\nPlease provide estimates of other costs that do not fit within any other cost headings.',NULL,NULL,'','','Other costs',NULL,'\0','\0',2,NULL,1,15);
INSERT INTO `question` VALUES (35,NULL,'','Please tell us if you have every applied for or received any other public sector funding for this project? You should also include details of any offers of funding you\'ve received.','Please tell us if you have received, or will receive any other public sector funding for this project.','What should I include in the other public funding section?','','','Other funding',NULL,'\0','\0',18,NULL,1,7);
INSERT INTO `question` VALUES (36,NULL,'',NULL,NULL,NULL,'\0','\0','FINANCE_SUMMARY_INDICATOR_STRING',NULL,'\0','\0',16,NULL,1,8);
INSERT INTO `question` VALUES (38,NULL,'\0','Please enter the funding level that you would like to apply for in this application',NULL,'What funding level should I enter?','\0','','Funding level',NULL,'\0','\0',17,NULL,1,7);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_form_input`
--

LOCK TABLES `question_form_input` WRITE;
/*!40000 ALTER TABLE `question_form_input` DISABLE KEYS */;
INSERT INTO `question_form_input` VALUES (1,1,0);
INSERT INTO `question_form_input` VALUES (2,2,0);
INSERT INTO `question_form_input` VALUES (3,3,0);
INSERT INTO `question_form_input` VALUES (4,4,0);
INSERT INTO `question_form_input` VALUES (5,5,0);
INSERT INTO `question_form_input` VALUES (6,6,0);
INSERT INTO `question_form_input` VALUES (7,7,0);
INSERT INTO `question_form_input` VALUES (8,8,0);
INSERT INTO `question_form_input` VALUES (9,9,0);
INSERT INTO `question_form_input` VALUES (11,11,0);
INSERT INTO `question_form_input` VALUES (12,12,0);
INSERT INTO `question_form_input` VALUES (13,13,0);
INSERT INTO `question_form_input` VALUES (5,14,1);
INSERT INTO `question_form_input` VALUES (15,15,0);
INSERT INTO `question_form_input` VALUES (16,16,0);
INSERT INTO `question_form_input` VALUES (6,17,1);
INSERT INTO `question_form_input` VALUES (8,18,1);
INSERT INTO `question_form_input` VALUES (20,20,0);
INSERT INTO `question_form_input` VALUES (21,21,0);
INSERT INTO `question_form_input` VALUES (22,22,0);
INSERT INTO `question_form_input` VALUES (23,23,0);
INSERT INTO `question_form_input` VALUES (24,24,0);
INSERT INTO `question_form_input` VALUES (25,25,0);
INSERT INTO `question_form_input` VALUES (26,26,0);
INSERT INTO `question_form_input` VALUES (27,27,0);
INSERT INTO `question_form_input` VALUES (28,28,0);
INSERT INTO `question_form_input` VALUES (29,29,0);
INSERT INTO `question_form_input` VALUES (30,30,0);
INSERT INTO `question_form_input` VALUES (31,31,0);
INSERT INTO `question_form_input` VALUES (32,32,0);
INSERT INTO `question_form_input` VALUES (33,33,0);
INSERT INTO `question_form_input` VALUES (34,34,0);
INSERT INTO `question_form_input` VALUES (35,35,0);
INSERT INTO `question_form_input` VALUES (36,36,0);
INSERT INTO `question_form_input` VALUES (38,38,0);
/*!40000 ALTER TABLE `question_form_input` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_status`
--

LOCK TABLES `question_status` WRITE;
/*!40000 ALTER TABLE `question_status` DISABLE KEYS */;
INSERT INTO `question_status` VALUES (2,'2015-10-02 17:37:31',NULL,'',1,1,1,NULL,11);
INSERT INTO `question_status` VALUES (3,NULL,'',NULL,1,NULL,NULL,1,28);
INSERT INTO `question_status` VALUES (4,NULL,'',NULL,1,NULL,NULL,1,33);
INSERT INTO `question_status` VALUES (6,'2015-10-02 17:46:54',NULL,'',1,1,1,NULL,13);
INSERT INTO `question_status` VALUES (7,NULL,'',NULL,1,NULL,NULL,5,29);
INSERT INTO `question_status` VALUES (9,'2015-09-25 00:27:25',NULL,'',1,1,1,NULL,4);
INSERT INTO `question_status` VALUES (10,'2015-10-02 17:03:42','','\0',5,NULL,NULL,10,12);
INSERT INTO `question_status` VALUES (12,'2015-10-02 17:03:28','','\0',5,NULL,NULL,10,11);
INSERT INTO `question_status` VALUES (13,'2015-10-02 17:04:05','','\0',5,NULL,NULL,10,13);
INSERT INTO `question_status` VALUES (14,'2015-10-02 17:04:24','','\0',5,NULL,NULL,10,1);
INSERT INTO `question_status` VALUES (15,'2015-10-02 17:05:51','','\0',5,NULL,NULL,10,2);
INSERT INTO `question_status` VALUES (16,'2015-10-02 17:06:06','','\0',5,NULL,NULL,10,3);
INSERT INTO `question_status` VALUES (17,'2015-10-02 17:06:20','','\0',5,NULL,NULL,10,4);
INSERT INTO `question_status` VALUES (18,'2015-10-02 17:06:42','','\0',5,NULL,NULL,10,5);
INSERT INTO `question_status` VALUES (19,'2015-10-02 17:06:57','','\0',5,NULL,NULL,10,6);
INSERT INTO `question_status` VALUES (20,'2015-10-02 17:07:17','','\0',5,NULL,NULL,10,7);
INSERT INTO `question_status` VALUES (21,'2015-10-02 17:07:31','','\0',5,NULL,NULL,10,8);
INSERT INTO `question_status` VALUES (22,'2015-10-02 17:07:50','','\0',5,NULL,NULL,10,15);
INSERT INTO `question_status` VALUES (23,'2015-10-02 17:08:03','','\0',5,NULL,NULL,10,16);
INSERT INTO `question_status` VALUES (24,NULL,'',NULL,1,NULL,NULL,1,30);
INSERT INTO `question_status` VALUES (26,NULL,'',NULL,1,NULL,NULL,1,29);
INSERT INTO `question_status` VALUES (28,NULL,'',NULL,1,NULL,NULL,1,31);
INSERT INTO `question_status` VALUES (30,NULL,'',NULL,1,NULL,NULL,1,32);
INSERT INTO `question_status` VALUES (32,NULL,'',NULL,1,NULL,NULL,1,34);
INSERT INTO `question_status` VALUES (34,NULL,'',NULL,1,NULL,NULL,5,28);
INSERT INTO `question_status` VALUES (36,NULL,'',NULL,1,NULL,NULL,5,30);
INSERT INTO `question_status` VALUES (39,NULL,'',NULL,1,NULL,NULL,9,28);
INSERT INTO `question_status` VALUES (41,NULL,'',NULL,1,NULL,NULL,9,32);
INSERT INTO `question_status` VALUES (43,NULL,'',NULL,1,NULL,NULL,9,30);
INSERT INTO `question_status` VALUES (45,NULL,'',NULL,1,NULL,NULL,5,31);
INSERT INTO `question_status` VALUES (47,NULL,'',NULL,1,NULL,NULL,5,32);
INSERT INTO `question_status` VALUES (49,NULL,'',NULL,1,NULL,NULL,5,33);
INSERT INTO `question_status` VALUES (51,NULL,'',NULL,1,NULL,NULL,5,34);
INSERT INTO `question_status` VALUES (53,NULL,'',NULL,1,NULL,NULL,9,29);
INSERT INTO `question_status` VALUES (55,NULL,'',NULL,1,NULL,NULL,9,31);
INSERT INTO `question_status` VALUES (57,NULL,'',NULL,1,NULL,NULL,9,33);
INSERT INTO `question_status` VALUES (59,NULL,'',NULL,1,NULL,NULL,9,34);
INSERT INTO `question_status` VALUES (61,NULL,NULL,NULL,1,1,1,NULL,1);
INSERT INTO `question_status` VALUES (62,NULL,NULL,NULL,1,1,1,NULL,2);
INSERT INTO `question_status` VALUES (63,NULL,NULL,NULL,1,1,1,NULL,3);
INSERT INTO `question_status` VALUES (64,NULL,NULL,NULL,1,1,1,NULL,5);
INSERT INTO `question_status` VALUES (65,NULL,NULL,NULL,1,1,1,NULL,6);
INSERT INTO `question_status` VALUES (66,NULL,NULL,NULL,1,1,1,NULL,7);
INSERT INTO `question_status` VALUES (67,NULL,NULL,NULL,1,1,1,NULL,8);
INSERT INTO `question_status` VALUES (68,NULL,NULL,NULL,1,1,1,NULL,12);
INSERT INTO `question_status` VALUES (69,NULL,NULL,NULL,1,1,1,NULL,15);
INSERT INTO `question_status` VALUES (70,NULL,NULL,NULL,1,1,1,NULL,16);
/*!40000 ALTER TABLE `question_status` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `response`
--

LOCK TABLES `response` WRITE;
/*!40000 ALTER TABLE `response` DISABLE KEYS */;
INSERT INTO `response` VALUES (1,'2015-09-18 10:33:27',1,1,1);
INSERT INTO `response` VALUES (4,'2015-09-18 10:35:56',1,12,1);
INSERT INTO `response` VALUES (5,'2015-09-18 10:35:16',1,15,1);
INSERT INTO `response` VALUES (6,'2015-09-18 10:35:16',1,16,1);
INSERT INTO `response` VALUES (7,'2015-09-18 10:33:27',1,2,1);
INSERT INTO `response` VALUES (8,'2015-09-18 10:33:27',1,3,1);
INSERT INTO `response` VALUES (9,'2015-09-18 10:33:27',1,4,1);
INSERT INTO `response` VALUES (10,'2015-09-18 10:26:33',1,5,1);
INSERT INTO `response` VALUES (11,'2015-09-18 10:26:33',1,6,1);
INSERT INTO `response` VALUES (12,'2015-09-18 10:26:33',1,7,1);
INSERT INTO `response` VALUES (13,'2015-09-18 10:26:33',1,8,1);
INSERT INTO `response` VALUES (15,'2015-09-18 10:35:56',1,11,1);
INSERT INTO `response` VALUES (18,'2015-09-18 10:30:39',1,13,1);
INSERT INTO `response` VALUES (19,'2015-10-02 17:03:28',5,11,10);
INSERT INTO `response` VALUES (20,'2015-10-02 17:03:42',5,12,10);
INSERT INTO `response` VALUES (21,'2015-10-02 17:04:24',5,1,10);
INSERT INTO `response` VALUES (22,'2015-10-02 17:05:51',5,2,10);
INSERT INTO `response` VALUES (23,'2015-10-02 17:06:06',5,3,10);
INSERT INTO `response` VALUES (24,'2015-10-02 17:06:20',5,4,10);
INSERT INTO `response` VALUES (25,'2015-10-02 17:04:05',5,13,10);
INSERT INTO `response` VALUES (26,'2015-10-02 17:06:42',5,5,10);
INSERT INTO `response` VALUES (27,'2015-10-02 17:06:54',5,6,10);
INSERT INTO `response` VALUES (28,'2015-10-02 17:07:17',5,7,10);
INSERT INTO `response` VALUES (29,'2015-10-02 17:07:31',5,8,10);
INSERT INTO `response` VALUES (30,'2015-10-02 17:07:50',5,15,10);
INSERT INTO `response` VALUES (31,'2015-10-02 17:08:03',5,16,10);
INSERT INTO `response` VALUES (35,'2015-10-08 21:05:45',1,38,1);
/*!40000 ALTER TABLE `response` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'leadapplicant');
INSERT INTO `role` VALUES (2,'collaborator');
INSERT INTO `role` VALUES (3,'assessor');
INSERT INTO `role` VALUES (4,'applicant');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `version_rank` int(11) NOT NULL,
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`version`),
  KEY `schema_version_vr_idx` (`version_rank`),
  KEY `schema_version_ir_idx` (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` VALUES (1,1,'1','BaseVersion','SQL','V1__BaseVersion.sql',-127363055,'root','2016-01-12 13:19:19',396,1);
INSERT INTO `schema_version` VALUES (2,2,'2','ReferenceData','SQL','V2__ReferenceData.sql',1738715997,'root','2016-01-12 13:19:19',27,1);
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `section`
--

LOCK TABLES `section` WRITE;
/*!40000 ALTER TABLE `section` DISABLE KEYS */;
INSERT INTO `section` VALUES (1,NULL,'Please provide Innovate UK with information about your project. These sections are not scored, but are required to provide background to the project.','\0','Details',1,1,NULL,'\0');
INSERT INTO `section` VALUES (2,NULL,'These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.','\0','Application questions',2,1,NULL,'\0');
INSERT INTO `section` VALUES (6,NULL,'Each partner is required to submit their own project finances and funding rates. The overall project costs for all partners can be seen in the Finances overview section','\0','Finances',6,1,NULL,'\0');
INSERT INTO `section` VALUES (7,NULL,NULL,'\0','Your finances',3,1,6,'');
INSERT INTO `section` VALUES (8,NULL,'This is the financial overview of all partners in this collaboration. Each partner should submit their organisations finances in \"your finances\" section. All partners will see this level of detail.','\0','Finances overview',4,1,6,'');
INSERT INTO `section` VALUES (9,NULL,NULL,'\0','Labour',1,1,7,'\0');
INSERT INTO `section` VALUES (10,NULL,NULL,'\0','Overheads',2,1,7,'\0');
INSERT INTO `section` VALUES (11,NULL,NULL,'\0','Materials',3,1,7,'\0');
INSERT INTO `section` VALUES (12,NULL,NULL,'\0','Capital usage',4,1,7,'\0');
INSERT INTO `section` VALUES (13,NULL,NULL,'\0','Subcontracting costs',5,1,7,'\0');
INSERT INTO `section` VALUES (14,NULL,NULL,'\0','Travel and subsistence',6,1,7,'\0');
INSERT INTO `section` VALUES (15,NULL,NULL,'\0','Other Costs',7,1,7,'\0');
/*!40000 ALTER TABLE `section` ENABLE KEYS */;
UNLOCK TABLES;

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
  `token` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `invite_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK_mtqx5podr73c7h25y9qqu96x2` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'steve.smith@empire.com','image.jpg','Steve Smith','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','123abc',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `user` VALUES (2,'jessica.doe@ludlow.co.uk','image2.jpg','Jessica Doe','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','456def',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `user` VALUES (3,'paul.plum@gmail.com','image3.jpg','Professor Plum','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','789ghi',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `user` VALUES (6,'competitions@innovateuk.gov.uk','image4.jpg','Comp Exec (Competitions)','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','123def',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `user` VALUES (7,'finance@innovateuk.gov.uk','image5.jpg','Project Finance Analyst (Finance)','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','123ghi',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `user` VALUES (8,'pete.tom@egg.com','image2.jpg','Pete Tom','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','867def',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `user` VALUES (9,'felix.wilson@gmail.com','image3.jpg','Felix Wilson','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','123qwe',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

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
  CONSTRAINT `FK_kbg0lkwwyivtraa6pm155q9lb` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_hovbl4knvvbdxktjlkkxnbuh0` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_organisation`
--

LOCK TABLES `user_organisation` WRITE;
/*!40000 ALTER TABLE `user_organisation` DISABLE KEYS */;
INSERT INTO `user_organisation` VALUES (1,3);
INSERT INTO `user_organisation` VALUES (2,4);
INSERT INTO `user_organisation` VALUES (3,2);
INSERT INTO `user_organisation` VALUES (8,6);
INSERT INTO `user_organisation` VALUES (9,2);
/*!40000 ALTER TABLE `user_organisation` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,4);
INSERT INTO `user_role` VALUES (2,4);
INSERT INTO `user_role` VALUES (3,3);
INSERT INTO `user_role` VALUES (8,4);
INSERT INTO `user_role` VALUES (1,4);
INSERT INTO `user_role` VALUES (2,4);
INSERT INTO `user_role` VALUES (3,3);
INSERT INTO `user_role` VALUES (8,4);
INSERT INTO `user_role` VALUES (9,3);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-01-12 13:19:21
