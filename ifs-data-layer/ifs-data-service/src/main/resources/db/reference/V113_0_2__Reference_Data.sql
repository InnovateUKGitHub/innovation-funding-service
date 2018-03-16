
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

LOCK TABLES `activity_state` WRITE;
/*!40000 ALTER TABLE `activity_state` DISABLE KEYS */;
INSERT INTO `activity_state` VALUES (1,'APPLICATION_ASSESSMENT','PENDING'),(2,'APPLICATION_ASSESSMENT','OPEN'),(3,'APPLICATION_ASSESSMENT','REJECTED'),(4,'APPLICATION_ASSESSMENT','READY_TO_SUBMIT'),(5,'APPLICATION_ASSESSMENT','SUBMITTED'),(6,'PROJECT_SETUP_PROJECT_DETAILS','PENDING'),(7,'PROJECT_SETUP_PROJECT_DETAILS','READY_TO_SUBMIT'),(8,'PROJECT_SETUP_PROJECT_DETAILS','SUBMITTED'),(12,'APPLICATION_ASSESSMENT','ACCEPTED'),(13,'PROJECT_SETUP_GRANT_OFFER_LETTER','PENDING'),(14,'PROJECT_SETUP_GRANT_OFFER_LETTER','ASSIGNED'),(15,'PROJECT_SETUP_GRANT_OFFER_LETTER','READY_TO_SUBMIT'),(16,'PROJECT_SETUP_GRANT_OFFER_LETTER','ACCEPTED'),(17,'PROJECT_SETUP','PENDING'),(18,'PROJECT_SETUP','ACCEPTED'),(19,'APPLICATION_ASSESSMENT','CREATED'),(20,'APPLICATION_ASSESSMENT','WITHDRAWN'),(21,'PROJECT_SETUP_VIABILITY','NOT_VERIFIED'),(22,'PROJECT_SETUP_VIABILITY','NOT_APPLICABLE'),(23,'PROJECT_SETUP_VIABILITY','ACCEPTED'),(24,'PROJECT_SETUP_ELIGIBILITY','NOT_VERIFIED'),(25,'PROJECT_SETUP_ELIGIBILITY','NOT_APPLICABLE'),(26,'PROJECT_SETUP_ELIGIBILITY','ACCEPTED'),(27,'APPLICATION','CREATED'),(28,'APPLICATION','OPEN'),(29,'APPLICATION','SUBMITTED'),(30,'APPLICATION','NOT_APPLICABLE'),(31,'APPLICATION','NOT_APPLICABLE_INFORMED'),(32,'APPLICATION','ACCEPTED'),(33,'APPLICATION','REJECTED'),(34,'ASSESSMENT_REVIEW','CREATED'),(35,'ASSESSMENT_REVIEW','PENDING'),(36,'ASSESSMENT_REVIEW','REJECTED'),(37,'ASSESSMENT_REVIEW','ACCEPTED'),(38,'ASSESSMENT_REVIEW','CONFLICT_OF_INTEREST'),(39,'PROJECT_SETUP_SPEND_PROFILE','PENDING'),(40,'PROJECT_SETUP_SPEND_PROFILE','CREATED'),(41,'PROJECT_SETUP_SPEND_PROFILE','SUBMITTED'),(42,'PROJECT_SETUP_SPEND_PROFILE','ACCEPTED'),(43,'PROJECT_SETUP_SPEND_PROFILE','REJECTED'),(44,'ASSESSMENT_REVIEW','WITHDRAWN'),(45,'ASSESSMENT_INTERVIEW_PANEL','CREATED'),(46,'ASSESSMENT_INTERVIEW_PANEL','PENDING'),(47,'ASSESSMENT_INTERVIEW_PANEL','SUBMITTED');
/*!40000 ALTER TABLE `activity_state` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `address_type` WRITE;
/*!40000 ALTER TABLE `address_type` DISABLE KEYS */;
INSERT INTO `address_type` VALUES (1,'REGISTERED'),(2,'OPERATING'),(3,'PROJECT'),(4,'BANK_DETAILS');
/*!40000 ALTER TABLE `address_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `ethnicity` WRITE;
/*!40000 ALTER TABLE `ethnicity` DISABLE KEYS */;
INSERT INTO `ethnicity` VALUES (1,'WHITE','White',1,1),(2,'MIXED','Mixed/Multiple ethnic groups',2,1),(3,'ASIAN','Asian/Asian British',3,1),(4,'BLACK','Black/African/Caribbean',4,1),(5,'BLACK_BRITISH','Black British',5,1),(6,'OTHER','Other ethnic group',6,1),(7,'NOT_STATED','Prefer not to say',7,1);
/*!40000 ALTER TABLE `ethnicity` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `form_input_type` WRITE;
/*!40000 ALTER TABLE `form_input_type` DISABLE KEYS */;
INSERT INTO `form_input_type` VALUES (5,'APPLICATION_DETAILS'),(22,'ASSESSOR_APPLICATION_IN_SCOPE'),(21,'ASSESSOR_RESEARCH_CATEGORY'),(23,'ASSESSOR_SCORE'),(11,'CAPITAL_USAGE'),(3,'DATE'),(6,'EMPTY'),(4,'FILEUPLOAD'),(7,'FINANCE'),(20,'FINANCE_UPLOAD'),(27,'FINANCIAL_OVERVIEW_ROW'),(28,'FINANCIAL_STAFF_COUNT'),(16,'FINANCIAL_SUMMARY'),(26,'FINANCIAL_YEAR_END'),(8,'LABOUR'),(10,'MATERIALS'),(19,'ORGANISATION_SIZE'),(24,'ORGANISATION_TURNOVER'),(14,'OTHER_COSTS'),(17,'OTHER_FUNDING'),(9,'OVERHEADS'),(18,'PERCENTAGE'),(25,'STAFF_COUNT'),(12,'SUBCONTRACTING'),(2,'TEXTAREA'),(1,'TEXTINPUT'),(13,'TRAVEL'),(15,'YOUR_FINANCE');
/*!40000 ALTER TABLE `form_input_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `form_validator` WRITE;
/*!40000 ALTER TABLE `form_validator` DISABLE KEYS */;
INSERT INTO `form_validator` VALUES (1,'org.innovateuk.ifs.validator.EmailValidator','EmailValidator'),(2,'org.innovateuk.ifs.validator.NotEmptyValidator','NotEmptyValidator'),(3,'org.innovateuk.ifs.validator.WordCountValidator','WordCountValidator'),(4,'org.innovateuk.ifs.validator.NonNegativeLongIntegerValidator','NonNegativeLongIntegerValidator'),(5,'org.innovateuk.ifs.validator.SignedLongIntegerValidator','SignedLongIntegerValidator'),(6,'org.innovateuk.ifs.validator.PastMMYYYYValidator','PastMMYYYYValidator'),(7,'org.innovateuk.ifs.validator.AssessorScoreValidator','AssessorScoreValidator'),(8,'org.innovateuk.ifs.validator.ResearchCategoryValidator','ResearchCategoryValidator'),(9,'org.innovateuk.ifs.validator.AssessorScopeValidator','AssessorScopeValidator');
/*!40000 ALTER TABLE `form_validator` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `organisation_size` WRITE;
/*!40000 ALTER TABLE `organisation_size` DISABLE KEYS */;
INSERT INTO `organisation_size` VALUES (1,'Micro or small'),(2,'Medium'),(3,'Large');
/*!40000 ALTER TABLE `organisation_size` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `organisation_type` WRITE;
/*!40000 ALTER TABLE `organisation_type` DISABLE KEYS */;
INSERT INTO `organisation_type` VALUES (1,'Business','UK based business.',NULL,''),(2,'Research','Higher education and organisations registered with Je-S.',NULL,''),(3,'Research and technology organisation (RTO)','Organisations which solely promote and conduct collaborative research and innovation.',NULL,''),(4,'Public sector, charity or non Je-S registered research organisation','A not-for-profit public sector body or charity working on innovation.',NULL,'');
/*!40000 ALTER TABLE `organisation_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `participant_status` WRITE;
/*!40000 ALTER TABLE `participant_status` DISABLE KEYS */;
INSERT INTO `participant_status` VALUES (2,'ACCEPTED'),(1,'PENDING'),(3,'REJECTED');
/*!40000 ALTER TABLE `participant_status` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `project_role` WRITE;
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
INSERT INTO `project_role` VALUES (3,'PROJECT_FINANCE_CONTACT'),(2,'PROJECT_MANAGER'),(1,'PROJECT_PARTNER');
/*!40000 ALTER TABLE `project_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `rejection_reason` WRITE;
/*!40000 ALTER TABLE `rejection_reason` DISABLE KEYS */;
INSERT INTO `rejection_reason` VALUES (1,'','Not available',1),(2,'','Conflict of interest',2),(3,'','Not my area of expertise',3);
/*!40000 ALTER TABLE `rejection_reason` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'leadapplicant','applicant/dashboard'),(2,'collaborator','applicant/dashboard'),(3,'assessor','assessment/assessor/dashboard'),(4,'applicant','applicant/dashboard'),(5,'comp_admin','management/dashboard'),(6,'system_registrar',''),(7,'system_maintainer',''),(8,'project_finance','management/dashboard'),(9,'finance_contact',NULL),(10,'partner',NULL),(11,'project_manager',NULL),(12,'competition_executive',NULL),(13,'innovation_lead','management/dashboard'),(14,'ifs_administrator','management/dashboard'),(15,'support','management/dashboard'),(16,'panel_assessor',NULL),(17,'interview_assessor',NULL),(18,'interview_lead_applicant',NULL);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;