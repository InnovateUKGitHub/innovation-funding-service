
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

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `address_type` WRITE;
/*!40000 ALTER TABLE `address_type` DISABLE KEYS */;
INSERT INTO `address_type` VALUES (1,'REGISTERED'),(2,'OPERATING'),(3,'PROJECT'),(4,'BANK_DETAILS');
/*!40000 ALTER TABLE `address_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `affiliation` WRITE;
/*!40000 ALTER TABLE `affiliation` DISABLE KEYS */;
/*!40000 ALTER TABLE `affiliation` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `alert` WRITE;
/*!40000 ALTER TABLE `alert` DISABLE KEYS */;
/*!40000 ALTER TABLE `alert` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `application_finance` WRITE;
/*!40000 ALTER TABLE `application_finance` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_finance` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `assessor_form_input_response` WRITE;
/*!40000 ALTER TABLE `assessor_form_input_response` DISABLE KEYS */;
/*!40000 ALTER TABLE `assessor_form_input_response` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `attachment` WRITE;
/*!40000 ALTER TABLE `attachment` DISABLE KEYS */;
/*!40000 ALTER TABLE `attachment` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `bank_details` WRITE;
/*!40000 ALTER TABLE `bank_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank_details` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `category_link` WRITE;
/*!40000 ALTER TABLE `category_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_link` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `competition_funder` WRITE;
/*!40000 ALTER TABLE `competition_funder` DISABLE KEYS */;
/*!40000 ALTER TABLE `competition_funder` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `competition_user` WRITE;
/*!40000 ALTER TABLE `competition_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `competition_user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `content_event` WRITE;
/*!40000 ALTER TABLE `content_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `content_event` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `content_group` WRITE;
/*!40000 ALTER TABLE `content_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `content_group` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `content_section` WRITE;
/*!40000 ALTER TABLE `content_section` DISABLE KEYS */;
/*!40000 ALTER TABLE `content_section` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cost` WRITE;
/*!40000 ALTER TABLE `cost` DISABLE KEYS */;
/*!40000 ALTER TABLE `cost` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cost_categorization` WRITE;
/*!40000 ALTER TABLE `cost_categorization` DISABLE KEYS */;
/*!40000 ALTER TABLE `cost_categorization` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cost_category` WRITE;
/*!40000 ALTER TABLE `cost_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `cost_category` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cost_category_group` WRITE;
/*!40000 ALTER TABLE `cost_category_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `cost_category_group` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cost_category_type` WRITE;
/*!40000 ALTER TABLE `cost_category_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `cost_category_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cost_group` WRITE;
/*!40000 ALTER TABLE `cost_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `cost_group` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `cost_time_period` WRITE;
/*!40000 ALTER TABLE `cost_time_period` DISABLE KEYS */;
/*!40000 ALTER TABLE `cost_time_period` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `ethnicity` WRITE;
/*!40000 ALTER TABLE `ethnicity` DISABLE KEYS */;
INSERT INTO `ethnicity` VALUES (1,'WHITE','White',1,1),(2,'MIXED','Mixed/Multiple ethnic groups',2,1),(3,'ASIAN','Asian/Asian British',3,1),(4,'BLACK','Black/African/Caribbean',4,1),(5,'BLACK_BRITISH','Black British',5,1),(6,'OTHER','Other ethnic group',6,1),(7,'NOT_STATED','Prefer not to say',7,1);
/*!40000 ALTER TABLE `ethnicity` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `file_entry` WRITE;
/*!40000 ALTER TABLE `file_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_entry` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `finance_check` WRITE;
/*!40000 ALTER TABLE `finance_check` DISABLE KEYS */;
/*!40000 ALTER TABLE `finance_check` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `finance_row` WRITE;
/*!40000 ALTER TABLE `finance_row` DISABLE KEYS */;
/*!40000 ALTER TABLE `finance_row` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `finance_row_meta_value` WRITE;
/*!40000 ALTER TABLE `finance_row_meta_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `finance_row_meta_value` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `form_input_response` WRITE;
/*!40000 ALTER TABLE `form_input_response` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_input_response` ENABLE KEYS */;
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

LOCK TABLES `invite` WRITE;
/*!40000 ALTER TABLE `invite` DISABLE KEYS */;
/*!40000 ALTER TABLE `invite` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `invite_organisation` WRITE;
/*!40000 ALTER TABLE `invite_organisation` DISABLE KEYS */;
/*!40000 ALTER TABLE `invite_organisation` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `keyword` WRITE;
/*!40000 ALTER TABLE `keyword` DISABLE KEYS */;
/*!40000 ALTER TABLE `keyword` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `lead_applicant_type` WRITE;
/*!40000 ALTER TABLE `lead_applicant_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `lead_applicant_type` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `monitoring_officer` WRITE;
/*!40000 ALTER TABLE `monitoring_officer` DISABLE KEYS */;
/*!40000 ALTER TABLE `monitoring_officer` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `organisation` WRITE;
/*!40000 ALTER TABLE `organisation` DISABLE KEYS */;
/*!40000 ALTER TABLE `organisation` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `organisation_address` WRITE;
/*!40000 ALTER TABLE `organisation_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `organisation_address` ENABLE KEYS */;
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

LOCK TABLES `partner_organisation` WRITE;
/*!40000 ALTER TABLE `partner_organisation` DISABLE KEYS */;
/*!40000 ALTER TABLE `partner_organisation` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `post_attachment` WRITE;
/*!40000 ALTER TABLE `post_attachment` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_attachment` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `process` WRITE;
/*!40000 ALTER TABLE `process` DISABLE KEYS */;
/*!40000 ALTER TABLE `process` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `process_outcome` WRITE;
/*!40000 ALTER TABLE `process_outcome` DISABLE KEYS */;
/*!40000 ALTER TABLE `process_outcome` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `process_role` WRITE;
/*!40000 ALTER TABLE `process_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `process_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `profile` WRITE;
/*!40000 ALTER TABLE `profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `project_finance` WRITE;
/*!40000 ALTER TABLE `project_finance` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_finance` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `project_role` WRITE;
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
INSERT INTO `project_role` VALUES (3,'PROJECT_FINANCE_CONTACT'),(2,'PROJECT_MANAGER'),(1,'PROJECT_PARTNER');
/*!40000 ALTER TABLE `project_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `project_user` WRITE;
/*!40000 ALTER TABLE `project_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_user` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `question_status` WRITE;
/*!40000 ALTER TABLE `question_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `question_status` ENABLE KEYS */;
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

LOCK TABLES `setup_status` WRITE;
/*!40000 ALTER TABLE `setup_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `setup_status` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `spend_profile` WRITE;
/*!40000 ALTER TABLE `spend_profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `spend_profile` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `thread` WRITE;
/*!40000 ALTER TABLE `thread` DISABLE KEYS */;
/*!40000 ALTER TABLE `thread` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `user_organisation` WRITE;
/*!40000 ALTER TABLE `user_organisation` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_organisation` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `verification_condition` WRITE;
/*!40000 ALTER TABLE `verification_condition` DISABLE KEYS */;
/*!40000 ALTER TABLE `verification_condition` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

