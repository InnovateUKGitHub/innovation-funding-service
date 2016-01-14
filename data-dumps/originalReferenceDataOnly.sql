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
INSERT INTO `form_input_type` VALUES (19,'organisation_size');
/*!40000 ALTER TABLE `form_input_type` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `organisation_type`
--

LOCK TABLES `organisation_type` WRITE;
/*!40000 ALTER TABLE `organisation_type` DISABLE KEYS */;
INSERT INTO `organisation_type` VALUES (1,'Business',NULL);
INSERT INTO `organisation_type` VALUES (2,'Research',NULL);
INSERT INTO `organisation_type` VALUES (3,'Public Sector',NULL);
INSERT INTO `organisation_type` VALUES (4,'Charity',NULL);
INSERT INTO `organisation_type` VALUES (5,'Academic',2);
INSERT INTO `organisation_type` VALUES (6,'Research & technology organisation (RTO)',2);
INSERT INTO `organisation_type` VALUES (7,'Catapult',2);
INSERT INTO `organisation_type` VALUES (8,'Public sector research establishment',2);
INSERT INTO `organisation_type` VALUES (9,'Research council institute',2);
/*!40000 ALTER TABLE `organisation_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-01-14 12:13:31
