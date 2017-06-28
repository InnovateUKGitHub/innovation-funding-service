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
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
INSERT  IGNORE INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`) VALUES (1,51,'A novel solution to an old problem','2016-03-01',5,1,NULL);
INSERT  IGNORE INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`) VALUES (2,20,'Providing sustainable childcare','2015-11-01',2,1,NULL);
INSERT  IGNORE INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`) VALUES (3,10,'Mobile Phone Data for Logistics Analytics','2015-11-01',3,1,NULL);
INSERT  IGNORE INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`) VALUES (4,43,'Using natural gas to heat homes','2015-11-01',4,1,NULL);
INSERT  IGNORE INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`) VALUES (5,20,'A new innovative solution','2015-11-01',2,1,NULL);
INSERT  IGNORE INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`) VALUES (6,23,'Security for the Internet of Things','2015-11-01',2,1,NULL);
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `application_finance`
--

LOCK TABLES `application_finance` WRITE;
/*!40000 ALTER TABLE `application_finance` DISABLE KEYS */;
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (1,1,3,'SMALL');
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (2,1,6,'SMALL');
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (3,1,4,'LARGE');
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (4,5,4,'SMALL');
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (5,2,3,'SMALL');
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (6,5,3,'SMALL');
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (7,5,6,'SMALL');
/*!40000 ALTER TABLE `application_finance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `assessor_feedback`
--

LOCK TABLES `assessor_feedback` WRITE;
/*!40000 ALTER TABLE `assessor_feedback` DISABLE KEYS */;
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (5,'asdf','Yes',16,25);
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (6,'qwer','3',16,21);
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (7,'zxcv asdf qwer asdf','7',16,22);
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (8,'','3',16,24);
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (9,'asdfg','3',16,23);
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (12,'','5',16,28);
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (14,'asdf qwer asdf ','No',22,25);
INSERT  IGNORE INTO `assessor_feedback` (`id`, `assessment_feedback`, `assessment_value`, `assessor_id`, `response_id`) VALUES (15,'weqr','10',22,21);
/*!40000 ALTER TABLE `assessor_feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `cost`
--

LOCK TABLES `cost` WRITE;
/*!40000 ALTER TABLE `cost` DISABLE KEYS */;
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (1,NULL,'Working days per year',NULL,232,NULL,1,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (2,50000,'','Manager',168,NULL,1,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (4,30000,'','Engineer',696,NULL,1,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (12,100,'','Powder',20,NULL,1,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (13,150000,'specialist consultant','Mr Francis Bois',0,NULL,1,32);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (15,NULL,'Working days per year',NULL,227,NULL,2,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (16,NULL,'Working days per year',NULL,227,NULL,3,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (17,45000,'','Manager',120,NULL,3,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (18,140,'','Raw materials',120,NULL,3,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (19,600,'','crucibles',6,NULL,1,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (20,250,'','valves',12,NULL,1,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (21,32000,'','Engineer',250,NULL,3,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (22,28000,'','Technician',220,NULL,3,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (23,50,'','components',30,NULL,3,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (24,150,'','Tooling',7,NULL,3,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (25,76800,'','Research engineer',132,NULL,2,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (26,5000,'Machining of parts','East engineering',0,NULL,2,32);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (27,400,'','Plates',5,NULL,2,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (28,200,'','Powder',45,NULL,2,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (29,NULL,'Working days per year',NULL,232,NULL,6,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (30,50000,'','Manager',168,NULL,6,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (31,30000,'','Engineer',696,NULL,6,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (32,100,'','Powder',20,NULL,6,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (33,150000,'specialist consultant','Mr Francis Bois',0,NULL,6,32);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (34,NULL,'Working days per year',NULL,227,NULL,7,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (35,NULL,'Working days per year',NULL,227,NULL,4,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (36,45000,'','Manager',120,NULL,4,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (37,140,'','Raw materials',120,NULL,4,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (38,600,'','crucibles',6,NULL,6,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (39,250,'','valves',12,NULL,6,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (40,32000,'','Engineer',250,NULL,4,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (41,28000,'','Technician',220,NULL,4,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (42,50,'','components',30,NULL,4,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (43,150,'','Tooling',7,NULL,4,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (44,76800,'','Research engineer',132,NULL,7,28);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (45,5000,'Machining of parts','East engineering',0,NULL,7,32);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (46,400,'','Plates',5,NULL,7,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (47,200,'','Powder',45,NULL,7,30);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (48,0,'Grant Claim','',50,NULL,1,38);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (49,0,'Grant Claim','',70,NULL,2,38);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (50,0,'Grant Claim','',70,NULL,3,38);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (51,0,'Accept Rate','NONE',23,NULL,1,29);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (52,0,'Accept Rate','NONE',24,NULL,2,29);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (53,0,'Accept Rate','NONE',25,NULL,3,29);
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (54,0,'Other Funding','Yes',NULL,NULL,1,35);
/*!40000 ALTER TABLE `cost` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `cost_value`
--

LOCK TABLES `cost_value` WRITE;
/*!40000 ALTER TABLE `cost_value` DISABLE KEYS */;
INSERT  IGNORE INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES (13,1,'France');
INSERT  IGNORE INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES (26,1,'UK');
INSERT  IGNORE INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES (33,1,'France');
INSERT  IGNORE INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES (45,1,'UK');
/*!40000 ALTER TABLE `cost_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `file_entry`
--

LOCK TABLES `file_entry` WRITE;
/*!40000 ALTER TABLE `file_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `form_input_response`
--

LOCK TABLES `form_input_response` WRITE;
/*!40000 ALTER TABLE `form_input_response` DISABLE KEYS */;
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (1,'2015-09-18 10:33:27','Within the Industry one issue has caused progress in the field to be stifled.  Up until now any advancement has been made by working around this anomaly.  \r\n\r\nWe propose to tackle the situation head on and develop a tool that will circumvent the problem entirely allowing development to advance.\r\n',1,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (4,'2015-09-18 10:35:56','Wastage in our industry can be attributed in no small part to one issue.  To date businesses have been reluctant to tackle that problem and instead worked around it.  That has stifled progress.\r\n\r\nThe end result of our project will be a novel tool to manage the issue and substantially reduce the wastage caused by it.\r\n',12,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (5,'2015-09-18 10:35:16','The amount of money needed to complete this project is large.  \r\n\r\nThe academic phase will require the acquisition of several pieces of lab equipment and the employ of additional graduates to ensure the experiments run smoothly.\r\n\r\nThe Materials and use of the fabricators equipment will account for approximately some of the budget.\r\n\r\nThe time and expertise required across all 3 partners to complete each phase will amount to the lion share of the cost.\r\n',15,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (6,'2015-09-18 10:35:16','Although steps are under way to get this project off the ground, investment by Innovate UK will stimulate Industry leads to supply additional resource funding and use of their facilities as required to advance the progress of this project.  \r\n\r\nUK Investment will ensure that we are able to retain the onward manufacture internally instead of needing to create international deals taking work and profit out of the country.\r\n',16,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (7,'2015-09-18 10:33:27','The issue affects the entire field, the market value of which is significant, and accounts for a percentage of its annual overhead.  A tool to reduce that amount by a significant proportion would be taken up by the entire industry.  \r\n\r\nAt this time no other solutions are available or near market giving us the leading edge on market share.  \r\n\r\nManufacture and development could remain in the United Kingdom with an international export market.\r\n',2,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (8,'2015-09-18 10:33:27','The planned end result of the project would be a fully implementable tool that could be rolled out to the industry. \r\n\r\n Once a fully tested prototype can be demonstrated we can start to take orders and travel to international Industry leaders the exhibit the value of the tool.\r\n',3,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (9,'2015-09-18 10:33:27','The tool will give the consortium the leading edge on the technology as currently no one else is close to considering developing a solution.  \r\n\r\nThe Industry will be able to reduce wastage by a significant degree and pass those savings on to the consumer or reinvest into advancing the field.\r\n  \r\nInside 2 years it is estimated that a very large sum of money can be recouped by using our solution\r\n',4,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (10,'2015-09-18 10:26:33','Our Academic partner has developed a technique to identify the precise cause of the issue and have been able to demonstrate it at a small scale.  Taking this information they will scale up the experiment to a percentage of industrial scale to confirm.  \r\n\r\nWe will take this data and engineer a solution to isolate the problem we will then collude with our fabrication partner to design and prototype a tool to correct the obstacle. \r\n',5,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (11,'2015-09-18 10:26:33','The project is based on Manchester?s novel IP that, for the first time, allows SLM to be conducted without any need for anchors/supports. A search of the literature has established that the advances proposed in this project on which RAMP are based are not available via any commercially available process. In addition, the development and supply of tailored materials by Empire and a dedicated high temperature rig by Ludlow will involve further, critical degrees of innovation that will help to build a portfolio of IP to enhance commercial exploitation. NB. RAMP does not simply reduce stresses by working at higher temperatures; it involves a novel step based on manufacturing engineering and material science that completely eliminates stresses.',6,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (12,'2015-09-18 10:26:33','Three principal risks exist at this stage:\r\n1.        The academic scale up fails to replicate the smaller results either entirely or not to a satisfactory level. In this event the academic partner would begin a controlled scale back to identify if the method needs to be altered during scale up to replicate results.  In the event that the issue can only be identified in the small scale we would build that into our planning for the isolation phase\r\n2.      During the phase to engineer a technique to isolate the issue we anticipate that there are inherent safety issues.  We have consulted an independent firm to ensure that suitable equipment and measures have been put in place to minimise the risk to staff.\r\n3.    Although we are confident that our fabrication partner is the best option for the prototype solution there is an indication that a particular piece of heavy machinery, currently unavailable in the UK will be required.  Our Fabrication partner has already investigated options for accessing this equipment in the Far East and an appropriate budget has been set aside to be used for this purpose if required.\r\n',7,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (13,'2015-09-18 10:26:33','The team consists of:\r\nUs - We have been supplying engineering solutions to the industry for a number of years and have been principal deliverers for various advancements within the field.  We are a suitably sized team capable of developing flexible solutions to a wide variety of clients internationally.\r\n\r\nAcademic ? the principal investigator and his team have been studying the issue for a number of years and have qualifications across the spectrum that apply to the field.  Together they have already identified several efficiencies that can be made within the industry.\r\n\r\nThe Fabricators ? Are a leading team within complex engineering fabrication.  They have work on multiple high budget engineering solutions and consist of a team of people with many years? experience in a range of industries\r\n',8,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (15,'2015-09-18 10:35:56','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',11,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (16,'2015-08-08 00:00:00','file.doc',14,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (17,'2015-08-08 00:00:00','file.pdf',17,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (18,'2015-09-18 10:30:39','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',13,1,1,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (19,'2015-10-02 17:03:28','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',11,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (20,'2015-10-02 17:03:42','Wastage in our industry can be attributed in no small part to one issue.  To date businesses have been reluctant to tackle that problem and instead worked around it.  That has stifled progress.\r\n\r\nThe end result of our project will be a novel tool to manage the issue and substantially reduce the wastage caused by it.\r\n',12,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (21,'2015-10-02 17:04:24','Within the Industry one issue has caused progress in the field to be stifled.  Up until now any advancement has been made by working around this anomaly.  \r\n\r\nWe propose to tackle the situation head on and develop a tool that will circumvent the problem entirely allowing development to advance.\r\n',1,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (22,'2015-10-02 17:05:51','The issue affects the entire field, the market value of which is significant, and accounts for a percentage of its annual overhead.  A tool to reduce that amount by a significant proportion would be taken up by the entire industry.  \r\n\r\nAt this time no other solutions are available or near market giving us the leading edge on market share.  \r\n\r\nManufacture and development could remain in the United Kingdom with an international export market.\r\n',2,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (23,'2015-10-02 17:06:06','The planned end result of the project would be a fully implementable tool that could be rolled out to the industry. \r\n\r\n Once a fully tested prototype can be demonstrated we can start to take orders and travel to international Industry leaders the exhibit the value of the tool.',3,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (24,'2015-10-02 17:06:20','The tool will give the consortium the leading edge on the technology as currently no one else is close to considering developing a solution.  \r\n\r\nThe Industry will be able to reduce wastage by a significant degree and pass those savings on to the consumer or reinvest into advancing the field.\r\n  \r\nInside 2 years it is estimated that a very large sum of money can be recouped by using our solution\r\n',4,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (25,'2015-10-02 17:04:05','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.',13,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (26,'2015-10-02 17:06:42','Our Academic partner has developed a technique to identify the precise cause of the issue and have been able to demonstrate it at a small scale.  Taking this information they will scale up the experiment to a percentage of industrial scale to confirm.  \r\n\r\nWe will take this data and engineer a solution to isolate the problem we will then collude with our fabrication partner to design and prototype a tool to correct the obstacle. \r\n',5,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (27,'2015-10-02 17:06:54','The project is based on Manchester?s novel IP that, for the first time, allows SLM to be conducted without any need for anchors/supports. A search of the literature has established that the advances proposed in this project on which RAMP are based are not available via any commercially available process. In addition, the development and supply of tailored materials by Empire and a dedicated high temperature rig by Ludlow will involve further, critical degrees of innovation that will help to build a portfolio of IP to enhance commercial exploitation. NB. RAMP does not simply reduce stresses by working at higher temperatures; it involves a novel step based on manufacturing engineering and material science that completely eliminates stresses.',6,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (28,'2015-10-02 17:07:17','Three principal risks exist at this stage:\r\n1.        The academic scale up fails to replicate the smaller results either entirely or not to a satisfactory level. In this event the academic partner would begin a controlled scale back to identify if the method needs to be altered during scale up to replicate results.  In the event that the issue can only be identified in the small scale we would build that into our planning for the isolation phase\r\n2.      During the phase to engineer a technique to isolate the issue we anticipate that there are inherent safety issues.  We have consulted an independent firm to ensure that suitable equipment and measures have been put in place to minimise the risk to staff.\r\n3.    Although we are confident that our fabrication partner is the best option for the prototype solution there is an indication that a particular piece of heavy machinery, currently unavailable in the UK will be required.  Our Fabrication partner has already investigated options for accessing this equipment in the Far East and an appropriate budget has been set aside to be used for this purpose if required.\r\n',7,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (29,'2015-10-02 17:07:31','The team consists of:\r\nUs - We have been supplying engineering solutions to the industry for a number of years and have been principal deliverers for various advancements within the field.  We are a suitably sized team capable of developing flexible solutions to a wide variety of clients internationally.\r\n\r\nAcademic ? the principal investigator and his team have been studying the issue for a number of years and have qualifications across the spectrum that apply to the field.  Together they have already identified several efficiencies that can be made within the industry.\r\n\r\nThe Fabricators ? Are a leading team within complex engineering fabrication.  They have work on multiple high budget engineering solutions and consist of a team of people with many years? experience in a range of industries\r\n',8,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (30,'2015-10-02 17:07:50','The amount of money needed to complete this project is large.  \r\n\r\nThe academic phase will require the acquisition of several pieces of lab equipment and the employ of additional graduates to ensure the experiments run smoothly.\r\n\r\nThe Materials and use of the fabricators equipment will account for approximately some of the budget.\r\n\r\nThe time and expertise required across all 3 partners to complete each phase will amount to the lion share of the cost.\r\n',15,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (31,'2015-10-02 17:08:03','Although steps are under way to get this project off the ground, investment by Innovate UK will stimulate Industry leads to supply additional resource funding and use of their facilities as required to advance the progress of this project.  \r\n\r\nUK Investment will ensure that we are able to retain the onward manufacture internally instead of needing to create international deals taking work and profit out of the country.\r\n',16,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (32,'2015-10-02 17:08:03','score',14,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (33,'2015-10-02 17:08:03','score',17,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (34,'2015-10-02 17:08:03','score',18,10,5,NULL);
INSERT  IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (35,'2015-10-08 21:05:45','2',38,1,1,NULL);
/*!40000 ALTER TABLE `form_input_response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `invite`
--

LOCK TABLES `invite` WRITE;
/*!40000 ALTER TABLE `invite` DISABLE KEYS */;
INSERT  IGNORE INTO `invite` (`id`, `email`, `hash`, `name`, `status`, `application_id`, `invite_organisation_id`) VALUES (1,'john@empire.com','1d92a6ace9030f2d992f47ea60529028fd49542dffd6b179f68fae072b4f1cc61f12a419b79a5267','John','SEND',1,1);
INSERT  IGNORE INTO `invite` (`id`, `email`, `hash`, `name`, `status`, `application_id`, `invite_organisation_id`) VALUES (2,'rogier@worth.systems','4e09372b85241cb03137ffbeb2110a1552daa1086b0bce0ff7d8ff5d2063c8ffc10e943acf4a3c7a','Rogier','SEND',1,2);
INSERT  IGNORE INTO `invite` (`id`, `email`, `hash`, `name`, `status`, `application_id`, `invite_organisation_id`) VALUES (3,'Michael@worth.systems','b157879c18511630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96','michael','SEND',1,2);
/*!40000 ALTER TABLE `invite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `invite_organisation`
--

LOCK TABLES `invite_organisation` WRITE;
/*!40000 ALTER TABLE `invite_organisation` DISABLE KEYS */;
INSERT  IGNORE INTO `invite_organisation` (`id`, `organisation_name`, `organisation_id`) VALUES (1,NULL,3);
INSERT  IGNORE INTO `invite_organisation` (`id`, `organisation_name`, `organisation_id`) VALUES (2,'Worth Internet Systems',NULL);
/*!40000 ALTER TABLE `invite_organisation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `organisation`
--

LOCK TABLES `organisation` WRITE;
/*!40000 ALTER TABLE `organisation` DISABLE KEYS */;
INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (1,'Nomensa',NULL,NULL,1);
INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (2,'Worth Internet Systems',NULL,NULL,1);
INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (3,'Empire Ltd',NULL,NULL,1);
INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (4,'Ludlow',NULL,NULL,1);
INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (5,'Manchester University',NULL,NULL,5);
INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (6,'EGGS',NULL,NULL,5);
INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (7,'AA Ltd',NULL,NULL,2);
/*!40000 ALTER TABLE `organisation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `organisation_address`
--

LOCK TABLES `organisation_address` WRITE;
/*!40000 ALTER TABLE `organisation_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `organisation_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `process`
--

LOCK TABLES `process` WRITE;
/*!40000 ALTER TABLE `process` DISABLE KEYS */;
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (1,NULL,'recommend','2015-10-01 11:42:40',NULL,'assessed','Assessment',7);
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (2,NULL,'','2015-09-22 14:34:16',NULL,'pending','Assessment',8);
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (3,NULL,'recommend','2015-10-08 15:16:19',NULL,'assessed','Assessment',16);
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (4,NULL,'','2015-10-01 11:42:54',NULL,'pending','Assessment',17);
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (5,NULL,'','2015-10-07 11:22:33',NULL,'pending','Assessment',20);
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (6,NULL,'','2015-10-07 11:22:33',NULL,'pending','Assessment',21);
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (7,NULL,'recommend','2015-10-08 16:31:00',NULL,'assessed','Assessment',22);
INSERT  IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (8,NULL,'','2015-10-07 11:22:33',NULL,'pending','Assessment',23);
/*!40000 ALTER TABLE `process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `process_outcome`
--

LOCK TABLES `process_outcome` WRITE;
/*!40000 ALTER TABLE `process_outcome` DISABLE KEYS */;
INSERT  IGNORE INTO `process_outcome` (`id`, `comment`, `description`, `outcome`, `outcome_type`, `process_id`, `process_index`) VALUES (1,NULL,'hey','YES','recommend',1,0);
INSERT  IGNORE INTO `process_outcome` (`id`, `comment`, `description`, `outcome`, `outcome_type`, `process_id`, `process_index`) VALUES (2,NULL,NULL,'YES','recommend',2,0);
INSERT  IGNORE INTO `process_outcome` (`id`, `comment`, `description`, `outcome`, `outcome_type`, `process_id`, `process_index`) VALUES (3,NULL,NULL,'YES','recommend',3,0);
INSERT  IGNORE INTO `process_outcome` (`id`, `comment`, `description`, `outcome`, `outcome_type`, `process_id`, `process_index`) VALUES (4,NULL,NULL,'YES','recommend',7,0);
/*!40000 ALTER TABLE `process_outcome` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `process_role`
--

LOCK TABLES `process_role` WRITE;
/*!40000 ALTER TABLE `process_role` DISABLE KEYS */;
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (1,1,3,1,1);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (2,2,3,1,1);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (3,3,3,1,1);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (4,4,3,1,1);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (5,1,4,2,2);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (6,4,4,2,2);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (7,3,2,3,3);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (8,4,3,3,3);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (9,1,6,2,8);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (10,5,3,1,1);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (11,6,4,1,2);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (12,5,4,2,2);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (13,5,6,2,8);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (15,2,2,3,3);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (16,5,2,3,3);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (17,6,2,3,3);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (18,1,2,3,9);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (19,2,2,3,9);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (20,3,2,3,9);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (21,4,2,3,9);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (22,5,2,3,9);
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (23,6,2,3,9);
/*!40000 ALTER TABLE `process_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `question_status`
--

LOCK TABLES `question_status` WRITE;
/*!40000 ALTER TABLE `question_status` DISABLE KEYS */;
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (2,'2015-10-02 17:37:31',NULL,'',1,1,1,NULL,11);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (3,NULL,'',NULL,1,NULL,NULL,1,28);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (4,NULL,'',NULL,1,NULL,NULL,1,33);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (6,'2015-10-02 17:46:54',NULL,'',1,1,1,NULL,13);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (7,NULL,'',NULL,1,NULL,NULL,5,29);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (9,'2015-09-25 00:27:25',NULL,'',1,1,1,NULL,4);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (10,'2015-10-02 17:03:42','','\0',5,NULL,NULL,10,12);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (12,'2015-10-02 17:03:28','','\0',5,NULL,NULL,10,11);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (13,'2015-10-02 17:04:05','','\0',5,NULL,NULL,10,13);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (14,'2015-10-02 17:04:24','','\0',5,NULL,NULL,10,1);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (15,'2015-10-02 17:05:51','','\0',5,NULL,NULL,10,2);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (16,'2015-10-02 17:06:06','','\0',5,NULL,NULL,10,3);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (17,'2015-10-02 17:06:20','','\0',5,NULL,NULL,10,4);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (18,'2015-10-02 17:06:42','','\0',5,NULL,NULL,10,5);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (19,'2015-10-02 17:06:57','','\0',5,NULL,NULL,10,6);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (20,'2015-10-02 17:07:17','','\0',5,NULL,NULL,10,7);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (21,'2015-10-02 17:07:31','','\0',5,NULL,NULL,10,8);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (22,'2015-10-02 17:07:50','','\0',5,NULL,NULL,10,15);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (23,'2015-10-02 17:08:03','','\0',5,NULL,NULL,10,16);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (24,NULL,'',NULL,1,NULL,NULL,1,30);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (26,NULL,'',NULL,1,NULL,NULL,1,29);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (28,NULL,'',NULL,1,NULL,NULL,1,31);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (30,NULL,'',NULL,1,NULL,NULL,1,32);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (32,NULL,'',NULL,1,NULL,NULL,1,34);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (34,NULL,'',NULL,1,NULL,NULL,5,28);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (36,NULL,'',NULL,1,NULL,NULL,5,30);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (39,NULL,'',NULL,1,NULL,NULL,9,28);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (41,NULL,'',NULL,1,NULL,NULL,9,32);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (43,NULL,'',NULL,1,NULL,NULL,9,30);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (45,NULL,'',NULL,1,NULL,NULL,5,31);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (47,NULL,'',NULL,1,NULL,NULL,5,32);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (49,NULL,'',NULL,1,NULL,NULL,5,33);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (51,NULL,'',NULL,1,NULL,NULL,5,34);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (53,NULL,'',NULL,1,NULL,NULL,9,29);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (55,NULL,'',NULL,1,NULL,NULL,9,31);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (57,NULL,'',NULL,1,NULL,NULL,9,33);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (59,NULL,'',NULL,1,NULL,NULL,9,34);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (61,NULL,NULL,NULL,1,1,1,NULL,1);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (62,NULL,NULL,NULL,1,1,1,NULL,2);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (63,NULL,NULL,NULL,1,1,1,NULL,3);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (64,NULL,NULL,NULL,1,1,1,NULL,5);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (65,NULL,NULL,NULL,1,1,1,NULL,6);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (66,NULL,NULL,NULL,1,1,1,NULL,7);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (67,NULL,NULL,NULL,1,1,1,NULL,8);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (68,NULL,NULL,NULL,1,1,1,NULL,12);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (69,NULL,NULL,NULL,1,1,1,NULL,15);
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (70,NULL,NULL,NULL,1,1,1,NULL,16);
/*!40000 ALTER TABLE `question_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `response`
--

LOCK TABLES `response` WRITE;
/*!40000 ALTER TABLE `response` DISABLE KEYS */;
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (1,'2015-09-18 10:33:27',1,1,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (4,'2015-09-18 10:35:56',1,12,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (5,'2015-09-18 10:35:16',1,15,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (6,'2015-09-18 10:35:16',1,16,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (7,'2015-09-18 10:33:27',1,2,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (8,'2015-09-18 10:33:27',1,3,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (9,'2015-09-18 10:33:27',1,4,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (10,'2015-09-18 10:26:33',1,5,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (11,'2015-09-18 10:26:33',1,6,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (12,'2015-09-18 10:26:33',1,7,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (13,'2015-09-18 10:26:33',1,8,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (15,'2015-09-18 10:35:56',1,11,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (18,'2015-09-18 10:30:39',1,13,1);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (19,'2015-10-02 17:03:28',5,11,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (20,'2015-10-02 17:03:42',5,12,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (21,'2015-10-02 17:04:24',5,1,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (22,'2015-10-02 17:05:51',5,2,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (23,'2015-10-02 17:06:06',5,3,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (24,'2015-10-02 17:06:20',5,4,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (25,'2015-10-02 17:04:05',5,13,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (26,'2015-10-02 17:06:42',5,5,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (27,'2015-10-02 17:06:54',5,6,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (28,'2015-10-02 17:07:17',5,7,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (29,'2015-10-02 17:07:31',5,8,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (30,'2015-10-02 17:07:50',5,15,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (31,'2015-10-02 17:08:03',5,16,10);
INSERT  IGNORE INTO `response` (`id`, `update_date`, `application_id`, `question_id`, `updated_by_id`) VALUES (35,'2015-10-08 21:05:45',1,38,1);
/*!40000 ALTER TABLE `response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (1,'steve.smith@empire.com','image.jpg','Steve Smith','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27',NULL,NULL,NULL,NULL,NULL,'ACTIVE','6b50cb4f-7222-33a5-99c5-8c068cd0b03c');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (2,'jessica.doe@ludlow.co.uk','image2.jpg','Jessica Doe','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27',NULL,NULL,NULL,NULL,NULL,'ACTIVE','7de841a5-0812-3de9-bb0a-72e2ac86ecfb');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (3,'paul.plum@gmail.com','image3.jpg','Professor Plum','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27',NULL,NULL,NULL,NULL,NULL,'ACTIVE','847ac08d-5486-3f3a-9e15-06303fb01ffb');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (6,'competitions@innovateuk.gov.uk','image4.jpg','Comp Exec (Competitions)','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27',NULL,NULL,NULL,NULL,NULL,'ACTIVE','6c1964bd-6915-35b5-9145-79541fde6a04');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (7,'finance@innovateuk.gov.uk','image5.jpg','Project Finance Analyst (Finance)','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27',NULL,NULL,NULL,NULL,NULL,'ACTIVE','b23decfc-f417-3448-80b7-818bd75c92e4');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (8,'pete.tom@egg.com','image2.jpg','Pete Tom','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27',NULL,NULL,NULL,NULL,NULL,'ACTIVE','61de6f46-c6c5-3228-a18a-039504ee5a7f');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (9,'felix.wilson@gmail.com','image3.jpg','Felix Wilson','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27',NULL,NULL,NULL,NULL,NULL,'ACTIVE','36ec131a-858f-3a52-ba08-6764055d5836');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_organisation`
--

LOCK TABLES `user_organisation` WRITE;
/*!40000 ALTER TABLE `user_organisation` DISABLE KEYS */;
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (1,3);
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (2,4);
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (3,2);
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (8,6);
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (9,2);
/*!40000 ALTER TABLE `user_organisation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (1,4);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (2,4);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (3,3);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (8,4);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (1,4);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (2,4);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (3,3);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (8,4);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (9,3);
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

-- Dump completed on 2016-03-14 14:38:59
