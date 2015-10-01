-- MySQL dump 10.13  Distrib 5.5.44, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: ifs
-- ------------------------------------------------------
-- Server version	5.5.44-0ubuntu0.14.04.1

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
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
INSERT  IGNORE INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`) VALUES (1,36,'A novel solution to an old problem','2015-11-01',1,1),(2,20,'Providing sustainable childcare','2015-11-01',2,1),(3,10,'Mobile Phone Data for Logistics Analytics','2015-11-01',3,1),(4,43,'Using natural gas to heat homes','2015-11-01',4,1),(5,20,'Analytical technologies for biopharmaceuticals','2015-11-01',2,1),(6,23,'Security for the Internet of Things','2015-11-01',2,1);
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `application_finance`
--

LOCK TABLES `application_finance` WRITE;
/*!40000 ALTER TABLE `application_finance` DISABLE KEYS */;
INSERT  IGNORE INTO `application_finance` (`id`, `application_id`, `organisation_id`) VALUES (1,1,3),(2,1,6),(3,1,4);
/*!40000 ALTER TABLE `application_finance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `application_status`
--

LOCK TABLES `application_status` WRITE;
/*!40000 ALTER TABLE `application_status` DISABLE KEYS */;
INSERT  IGNORE INTO `application_status` (`id`, `name`) VALUES (1,'created'),(2,'submitted'),(3,'approved'),(4,'rejected');
/*!40000 ALTER TABLE `application_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `assessment`
--

LOCK TABLES `assessment` WRITE;
/*!40000 ALTER TABLE `assessment` DISABLE KEYS */;
INSERT IGNORE INTO `assessment` (`comments`,`temp_total_score`,`temp_recommended_value`,`submitted`,`recommendation_feedback`,`process_id`,`application`,`assessor`) VALUES ('',60,'YES','N','hey',1,1,3), (NULL,82,'YES','N',NULL,2,4,3), ('',40,'EMPTY','N','',3,5,3), (NULL,0,'EMPTY','N',NULL,4,6,3);
/*!40000 ALTER TABLE `assessment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `competition`
--

LOCK TABLES `competition` WRITE;
/*!40000 ALTER TABLE `competition` DISABLE KEYS */;
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`) VALUES (1,'2015-12-31','2015-11-12','Innovate UK is to invest up to £9 million in collaborative research and development to stimulate innovation in integrated transport solutions for local authorities. The aim of this competition is to meet user needs by connecting people and/or goods to transport products and services. New or improved systems will be tested in environment laboratories.','2015-11-11','Technology Inspired','2015-06-24');
/*!40000 ALTER TABLE `competition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `cost`
--

LOCK TABLES `cost` WRITE;
/*!40000 ALTER TABLE `cost` DISABLE KEYS */;
INSERT  IGNORE INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES (1,NULL,'Working days per year',NULL,232,1,28),(2,2444,'','Project cost 1',234,1,28),(4,2300,'','Project cost 2',200,1,28),(11,233,'','',22,1,28),(12,0,'','',0,1,30),(13,59990,'','',0,1,32),(15,NULL,'Working days per year',NULL,232,2,28),(16,NULL,'Working days per year',NULL,243,3,28),(17,30020,'','',6,3,28),(18,10,'','',7,3,30);
/*!40000 ALTER TABLE `cost` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `cost_field`
--

LOCK TABLES `cost_field` WRITE;
/*!40000 ALTER TABLE `cost_field` DISABLE KEYS */;
INSERT  IGNORE INTO `cost_field` (`id`, `title`, `type`) VALUES (1,'country','String');
/*!40000 ALTER TABLE `cost_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `cost_value`
--

LOCK TABLES `cost_value` WRITE;
/*!40000 ALTER TABLE `cost_value` DISABLE KEYS */;
INSERT  IGNORE INTO `cost_value` (`cost_id`, `cost_field_id`, `value`) VALUES (13,1,'test454');
/*!40000 ALTER TABLE `cost_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `organisation`
--

LOCK TABLES `organisation` WRITE;
/*!40000 ALTER TABLE `organisation` DISABLE KEYS */;
INSERT  IGNORE INTO `organisation` (`id`, `name`) VALUES (1,'Nomensa'),(2,'Worth Internet Systems'),(3,'Empire Ltd'),(4,'Ludlow'),(5,'Manchester University'),(6,'EGGS'),(7,'AA Ltd');
/*!40000 ALTER TABLE `organisation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `process`
--

LOCK TABLES `process` WRITE;
/*!40000 ALTER TABLE `process` DISABLE KEYS */;
INSERT IGNORE INTO `process` (`id`,`decision_reason`,`end_date`,`event`,`last_modified`,`observations`,`start_date`,`status`) VALUES (1,NULL,NULL,'ASSESSMENT','2015-10-01 11:42:40',NULL,NULL,'assessed'), (2,NULL,NULL,'ASSESSMENT','2015-09-22 14:34:16',NULL,NULL,'pending'), (3,NULL,NULL,'ASSESSMENT','2015-10-01 11:43:01',NULL,NULL,'open'), (4,'not-my-area-of-interest',NULL,'ASSESSMENT','2015-10-01 11:42:54','',NULL,'pending');
/*!40000 ALTER TABLE `process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `process_role`
--

LOCK TABLES `process_role` WRITE;
/*!40000 ALTER TABLE `process_role` DISABLE KEYS */;
INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (1,1,3,1,1),(2,2,3,1,1),(3,3,3,1,1),(4,4,3,1,1),(5,1,4,2,2),(6,4,4,2,2),(7,3,2,3,3),(8,4,3,3,3),(9,1,6,2,8),(10,5,4,1,2),(11,6,4,1,2);
/*!40000 ALTER TABLE `process_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT  IGNORE INTO `question` (`id`, `assessor_confirmation_question`, `assign_enabled`, `description`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `option_values`, `priority`, `word_count`, `competition_id`, `question_type_id`, `section_id`) VALUES (1,NULL,'',NULL,'<p>You should describe:</p><ul class=\"list-bullet\">         <li>the business opportunity you have identified and how you plan to take advantage of it</li><li>the customer needs you have identified and how your project will meet them</li><li>the challenges you expect to face and how you will overcome them</li></ul>','What should I include in the business opportunity section?','','\0','1. What is the business opportunity that your project addresses?','','',NULL,1,500,1,2,3),(2,NULL,'',NULL,'<p>Describe the size of the potential market for your project, including:</p><ul class=\"list-bullet\">         <li>details of your target market, for instance, how competitive and profitable it is</li><li>the current size of the market, with actual and predicted growth rates</li><li>the market share you expect to achieve and the reasons for this estimate</li><li>the wider economic value you expect your project to add to the UK and/or the EEA (European Economic Area)</li></ul><p>Tell us what return on investment you expect your project to achieve. You should base this estimate on relevant industry data and tell us how you have calculated this.</p><p>If you are targeting an undeveloped market, you should also:</p><ul class=\"list-bullet\">         <li>describe how you plan to access this market</li><li>estimate its potential size</li><li>explain how you will explore its potential</li></ul>','What should I include in the market opportunity section?','','\0','2. What is the size of the potential market for your project?','','',NULL,2,500,1,2,3),(3,NULL,'',NULL,'<p>Describe the potential outputs of the project, such as:</p><ul class=\"list-bullet\">         <li>products or services</li><li>processes</li><li>applications</li></ul><p>Describe how you will exploit these outputs, such as:</p><ul class=\"list-bullet\">         <li>the route to market</li><li>protection of intellectual property rights</li><li>reconfiguration of your organisation\'s value system</li><li>changes to business models and processes</li><li>any other methods of exploitation and protection</li></ul> ','What should I include in the project exploitation section?','','\0','3. How will you exploit and market your project?','','',NULL,3,500,1,2,3),(4,NULL,'',NULL,'<p>Describe all the benefits you expect your project to deliver, including:</p><strong>Economic</strong> – this is the real impact the project will have on its economic environment. This is not traditional corporate accounting profit and can include cost avoidance. You should identify and quantify any expected benefits to:</p><ul class=\"list-bullet\">         <li>users (intermediaries and end users)</li><li>suppliers</li><li>broader industrial markets</li><li>the UK economy</li></ul><p><strong>Social</strong> - quantify any expected social impacts, either positive or negative, on, for example:</p><ul class=\"list-bullet\">         <li>quality of life</li><li>social inclusion/exclusion</li><li>education</li><li>public empowerment</li><li>health and safety</li><li>regulation</li><li>diversity</li><li>government priorities</li></ul><p><strong>Environmental</strong> – show how your project will benefit the environment or have low impact. For example, this could include:<p><ul class=\"list-bullet\">         <li>careful management of energy consumption</li><li>reductions in carbon emissions</li><li>reducing manufacturing and materials waste</li></li>rendering waste less toxic before disposing of it in a safe and legal manner</li><li>re-manufacturing (cradle to cradle)</li></ul>','What should I include in the benefits section?','','\0','4. What economic, social and environmental benefits do you expect your project to deliver and when?','','',NULL,4,500,1,2,3),(5,NULL,'','Describe the areas of work and your objectives. List all resource and management needs. Provide an overview of your technical approach.','<p>You should:</p><ul class=\"list-bullet\">         <li>describe your technical approach including the main objectives of the work</li><li>explain how and why your approach is appropriate</li><li>tell us how you will ensure that the innovative steps in your project are achievable</li><li>describe rival technologies and alternative R&D strategies</li><li>explain why your proposed approach will offer a better outcome</li></ul>','What should I include in the technical approach section?','','\0','5. What technical approach will you use and how will you manage your project?',NULL,NULL,NULL,1,500,1,2,4),(6,NULL,'','Explain how your project is innovative in both a commercial and technical sense.','<p>You should show how your project will:</p><ul class=\"list-bullet\">         <li>push boundaries beyond current leading-edge science and technology</li><li>apply existing technologies in new areas</li></ul><p>Explain the novelty of the research in an industrial and/or academic context.</p><p>You should provide evidence that your proposed work is innovative. This could include patent search results, competitor analyses or literature surveys. If relevant, you should also outline your own intellectual property rights.</p>','What should I include in the project innovation section?','','\0','6. What is innovative about your project?',NULL,NULL,NULL,5,500,1,2,4),(7,NULL,'','We recognise that many of the projects we fund are risky. This is why we need to be sure that you have an adequate plan for managing this risk.','<p>Please describe your plans for limiting and managing risk. You need to:</p><ul class=\"list-bullet\">         <li>identify the project\'s main risks and uncertainties</li><li>detail specific technical, commercial, managerial and environmental risks</li><li>list any other uncertainties such as ethical issues associated with the project</li><li>provide a detailed risk analysis</li><li>rate the main risks as high/medium/low</li><li>show how you\'ll limit the main risks</li><li>identify the project management tools and mechanisms you\'ll use to minimise operational risk</li><li>include arrangements for managing the project team and its partners</li></ul>','What should I include in the project risks section?','','\0','7. What are the risks (technical, commercial and environmental) to your project\'s success? What is your risk management strategy?',NULL,NULL,NULL,10,500,1,2,4),(8,NULL,'','Describe your capability to develop and exploit this technology. Include details of your team\'s track record in managing research and development projects.','<p>You should show your project team:</p><ul class=\"list-bullet\">         <li>has the right mix of skills and experience to complete the project</li><li>has clear objectives</li><li>how it would have been formed even without Innovate UK investment</li></ul><p>If you are part of a consortium, describe the benefits of the collaboration. For example, increased knowledge transfer.</p>','What should I include in the project skills section?','','\0','8. Does your project team have the skills, experience and facilities to deliver this project?',NULL,NULL,NULL,15,500,1,2,4),(9,NULL,'\0','Enter the full title of the project',NULL,NULL,'\0','\0','Application details',NULL,NULL,NULL,1,500,1,5,1),(11,NULL,'','Please provide a short summary of your project. Make sure you include what is innovative about it.','<p>We will not score this summary, but it will give the assessors a useful introduction to your project. It should provide a clear overview of the whole project, including:</p> <ul class=\"list-bullet\">         <li>your vision for the project</li><li>key objectives</li><li>main areas of focus</li><li>details of how it is innovative</li></ul>','What should I include in the project summary?','','\0','Project summary',NULL,NULL,NULL,1,500,1,2,1),(12,NULL,'','Please provide a brief description of your project. If your application is successful, we will publish this description. This question is mandatory but we will not assess this content as part of your application.','<p>Innovate UK publishes information about projects we have funded. This is in line with government practice on openness and transparency of public-funded activities.</p><p>Describe your project in a way that will be easy for a non-specialist to understand. Don\'t include any information that is confidential, for example, intellectual property or patent details.</p> ','What should I include in the project public description?','','\0','Public description',NULL,NULL,NULL,2,500,1,2,1),(13,'Is this application in scope?','','If your application doesn\'t align with the scope, we will reject it.','<p>It is important that you read the following guidance.</p><p>To show how your project aligns with the scope of this competition, you need to:</p><ul class=\"list-bullet\">         <li>read the competition brief in full</li><li>understand the background, challenge and scope of the competition</li><li>address the research objectives in your application</li><li>match your project\'s objectives and activities to these</li></ul> <p>Once you have submitted your application, you should not change this section unless:</p><ul class=\"list-bullet\">         <li>we ask you to provide more information</li><li>we ask you to make it clearer</li></ul> ','What should I include in the project scope?','','\0','How does your project align with the scope of this competition?','',NULL,NULL,1,500,1,2,2),(14,NULL,'','If you need to provide more information to support your answer to this question, you can include an appendix.','<p>You should include only supporting information in the appendix. You shouldn\'t use it to provide responses to the question.</p><p>You can include a Gantt chart of your proposed project</p><p>The appendix must:</p><ul class=\"list-bullet\">         <li>be in a Portable Document Format (.pdf)</li><li>be legible at 100% magnification</li><li>display the application number and project title at the top of the document</li><li>be no more than 6 sides of A4 - if your appendix is longer than this, we will assess only the first 6 pages</li></ul>','What should I include in the Question 5 appendix?','\0','\0',' ','','',NULL,2,500,1,4,4),(15,NULL,'','Tell us the toal costs of the project and how much funding you need from Innovate UK. Please provide details of your expected project costs along with any supporting information. Please justify any large expenditure in your project.','<p>You must:</p><ul class=\"list-bullet\">         <li>show how your budget is realistic for the scale and complexity of the project</li><li>make sure the funding you need from Innovate UK is within the limit set by this competition</li><li>justify any significant costs in the project, such as subcontractors</li><li>show how much funding there will be from other sources</li><li>provide a realistic budget breakdown</li><li>describe and justify individual work packages</li></ul><p>Find out which project costs are eligible: https://interact.innovateuk.org/-/project-costs</p><p>If your project spans more than one type of research category, you must break down the costs as separate \'work packages\'. For example, industrial research or experimental development. </p><p>You can find more information in the guidance section of your website:\n https://interact.innovateuk.org/-/funding-rules</p>','What should I include in the project cost section?','','\0','9. What will your project cost?','','',NULL,4,500,1,2,5),(16,NULL,'',' ','Justify why you\'re unable to fund the project yourself from commercial resources. Explain the difference this funding will make to your project. For example, will it lower the risk for you or speed up the process of getting your product to market? Tell us why this will benefit the UK.','What should I include in the financial support from Innovate UK section?','','\0','10. How does financial support from Innovate UK and its funding partners add value?','','',NULL,5,500,1,2,5),(17,NULL,'','If you need to provide more information to support your answer, you can include an appendix.','<p>You should include only supporting information in the appendix. You shouldn\'t use it to provide your responses to the question.</p><p>The appendix must:</p><ul class=\"list-bullet\">         <li>be in a Portable Document Format (.pdf)</li><li>be legible at 100% magnification</li><li>display the application number and project title at the top of the document</li><li>be no more than 6 sides of A4 - if your appendix is longer than this, we will assess only the first 6 pages</li></ul><p>Upload a file, for instance, a diagram of innovation.</p>','What should I include in the Question 6 appendix?','\0','\0',' ','','',NULL,6,0,1,4,4),(18,NULL,'','If you need to provide more information to support your answer, you can include an appendix.','<p>You should include only supporting information in the appendix. You shouldn\'t use it to provide responses to the question.</p><p>You can describe the expertise and track record of project partners and sub-contractors. Academic collaborators can include details of their research achievements.</p><p>The appendix must:</p><ul class=\"list-bullet\">         <li>be in a Portable Document Format (.pdf)</li><li>be legible at 100% magnification</li><li>display the application number and project title at the top of the document</li><li>be no more than 6 sides of A4 - if your appendix is longer than this, we will assess only the first 6 pages</li></ul>','What should I include in the Question 8 appendix?','\0','\0',' ','','',NULL,17,0,1,4,4),(19,NULL,'','<p>We will fund projects between £400,000 and £1 million. We may consider projects with costs outside of this range. We expect projects to last between 12 and 24 months.</p><p>Business partners can claim funding at the following rates:</p><ul class=\"list-bullet\">         <li>small businesses - 70% of total eligible costs</li>         <li>medium size businesses – 60% of total eligible costs</li>         <li>large - 50% of total eligible costs</li>     </ul><hr style=\"border: 1px solid #bfc1c3;\">',NULL,NULL,'\0','\0','Funding rules for this competition',NULL,NULL,NULL,1,0,1,7,6),(20,NULL,'','','','','\0','\0',NULL,NULL,NULL,NULL,1,0,1,15,7),(21,NULL,'','<p>You may claim the labour costs of all individuals you have working on your project.</p> <p> If your application is awarded funding, you will need to account for all your labour costs as they occur. For example, you should keep timesheets and payroll records. These should show the actual hours worked by individuals and paid by the organisation.</p>','<p>You can include the following labour costs, based upon your PAYE records:</p> <ul class=\"list-bullet\">         <li>gross salary</li><li>National Insurance</li><li>company pension contribution</li><li>life insurance</li><li>other non-discretionary package costs.</li>     </ul><p>You can\'t include:</p><ul class=\"list-bullet\">         <li>discretionary bonuses</li><li>performance related payments of any kind</li></ul> <p>We base the total number of working days per year on full time days less standard holiday allowance. You should not include:</p><ul class=\"list-bullet\">         <li>sick days</li><li>waiting time</li><li>training days</li><li>non-productive time</li></ul> <p>On the finance form, list the total days worked by all categories of staff on your project. Describe their role.</p><p>We will review the total amount of time and cost before we approve your application. The terms and conditions of the grant include compliance with these points.','Labour costs guidance','\0','',NULL,NULL,NULL,NULL,1,0,1,6,9),(22,NULL,'','<p>Overheads are incremental indirect expenses incurred as a result of delivering the project. They are eligible for project funding.</p>','We will review and test your calculation. This is so that we can make sure that the items included are eligible and reasonable</p><p>Please read our guide to claiming Overheads for further information.</p>','Overheads guidance','\0','',NULL,NULL,NULL,NULL,1,NULL,1,6,10),(23,NULL,'','<p>You can claim the costs of materials used on your project providing:</p><ul class=\"list-bullet\">         <li>they are not already purchased or included in the overheads</li><li>they won\'t have a residual/resale value at the end of your project. If they do, you can claim the costs minus this value.</li></ul><p>Please refer to our guide to project costs for further information.</p>','If you are using materials supplied by associated companies or sub contracted from other consortium members then you are required to exclude the profit element of the value placed on that material - the materials should be charged at cost.\n\nSoftware that you have purchased specifically for use during your project should be included in materials.\n\nHowever if you already own software which will be used in the project, or it is provided for usage within your consortium by a consortium member, only additional costs incurred & paid between the start and end of your project will be eligible. Examples of costs that may be eligible are those related to the preparation of disks, manuals, installation, training or customisation.','Materials costs guidance','\0','',NULL,NULL,NULL,NULL,1,NULL,1,6,11),(24,NULL,'','<p>Capital usage refers to an asset that you will use in your project. The asset will have a useful life of more than one year, be stand-alone, distinct and moveable.</p>','<p>You should provide details of capital equipment and tools you will buy for, or use on, your project.</p><p>You will need to calculate a ‘usage’ value for each item. You can do this by deducting its expected value at the end of your project from its original price. If you owned the equipment before you started the project, you should use its Net Present Value.</p><p>This value is then multiplied by the percentage that your project will be utilising the equipment. This final value represents the eligible cost to your project.</p>','Capital usage guidance','\0','',NULL,NULL,NULL,NULL,1,NULL,1,6,12),(25,NULL,'','<p>Subcontract costs relate to work carried out by third party organisations. These organisations are not part of your project or collaboration. You may subcontract work if you don\'t have the expertise in your consortium. You can also subcontract if it is cheaper than developing your skills in-house.</p>','Subcontract services supplied by associated companies should exclude any profit element and be charged at cost.\n\nYou should name the subcontractor (where known) and describe what the subcontractor will be doing and where the work will be undertaken. We will look at the size of this contribution when assessing eligibility and level of support.\n','Subcontracting costs guidance','\0','',NULL,NULL,NULL,NULL,1,NULL,1,6,13),(26,NULL,'','<p>You should include travel and subsistence costs that relate only to this project. </p>',NULL,NULL,'\0','',NULL,NULL,NULL,NULL,1,NULL,1,6,14),(27,NULL,'','<p>You can use this section to detail costs that do not fit under the other cost headings. You should describe each type of cost and explain why you have included it.</p>','<p>Examples of other costs include:</p><p><strong>Training costs</strong> – these costs are eligible for support if they relate to your project. We may support management training for your project but will not support ongoing training.</p><p><strong>Preparation of technical reports</strong> – for example, if the main aim of your project is standards support or technology transfer. You should show how this is more than you would produce through good project management.</p><p><strong>Market assessment</strong> – we may support of market assessments studies. The study will need to help us understand how your project is a good match for your target market. It could also be eligible if it helps commercialise your product</p></p><strong>Licensing in new technologies</strong> – if new technology makes up a large part of your project, we will expect you to develop that technology. For instance, if the value of the technology is more than £100,000. </p><p><strong>Patent filing costs for NEW IP generated by your project</strong> - these are eligible for SMEs up to a limit of £7,500 per partner. You should not include legal costs relating to the filing or trademark related expenditure.</p><p>Regulatory compliance costs are eligible if necessary to carry out your project.</p>','Other costs guidance','\0','',NULL,NULL,NULL,NULL,1,NULL,1,6,15),(28,NULL,'',NULL,NULL,NULL,'','','Labour',NULL,NULL,NULL,2,NULL,1,8,9),(29,NULL,'','A common way of estimating overheads on Innovate UK projects is to use a flat rate of 20% of labour costs. This is the default rate. If you want to use a different rate, please enter it below and we will assess your overheads on this basis.  If your project is successful, you will need to justify this rate to our finance team. Two methods of declaring overheads are available:',NULL,NULL,'','','Overheads',NULL,NULL,NULL,2,NULL,1,9,10),(30,NULL,'','Please provide a breakdown of the materials you expect to use during the project',NULL,NULL,'','','Materials',NULL,NULL,NULL,2,NULL,1,10,11),(31,NULL,'','Please provide a breakdown of the capital items you will buy and/or use for the project.',NULL,NULL,'','','Capital Usage',NULL,NULL,NULL,2,NULL,1,11,12),(32,NULL,'','Please provide details of any work that you expect to subcontract for your project.',NULL,NULL,'','','Sub-contracting costs',NULL,NULL,NULL,2,NULL,1,12,13),(33,NULL,'',NULL,NULL,NULL,'','','Travel and subsistence',NULL,NULL,NULL,2,NULL,1,13,14),(34,NULL,'','Please note that legal or project audit and accountancy fees are not eligible and should not be included as an \'other cost\'.Patent filing costs of NEW IP relating to the project are limited to £5,000 for SME applicants only.\n\nPlease provide estimates of other costs that do not fit within any other cost headings.',NULL,NULL,'','','Other costs',NULL,NULL,NULL,2,NULL,1,14,15),(35,NULL,'','Please tell us if you have every applied for or received any other public sector funding for this project? You should also include details of any offers of funding you\'ve received.','You don\'t need to include completed grants for projects that have helped you develop earlier passes of your idea. You should note that we count other public sector support as part of the grant you can receive for your project.','What should I include in the other public funding section?','\0','','Other funding',NULL,NULL,NULL,2,NULL,1,17,7),(36,NULL,'',NULL,NULL,NULL,'\0','\0',NULL,NULL,NULL,NULL,1,NULL,1,16,8);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `question_status`
--

LOCK TABLES `question_status` WRITE;
/*!40000 ALTER TABLE `question_status` DISABLE KEYS */;
INSERT  IGNORE INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (1,NULL,'',NULL,1,NULL,NULL,1,12),(2,NULL,'\0',NULL,1,NULL,NULL,1,11),(3,NULL,'',NULL,1,NULL,NULL,1,28),(4,NULL,'',NULL,1,NULL,NULL,1,33),(5,'2015-09-24 18:49:20','','\0',1,NULL,NULL,NULL,1),(6,'2015-09-25 00:06:12',NULL,'',1,1,5,NULL,13),(7,NULL,'\0',NULL,1,NULL,NULL,5,29),(8,'2015-09-25 00:07:50',NULL,'\0',1,NULL,NULL,NULL,29),(9,'2015-09-25 00:27:25',NULL,'\0',1,1,5,NULL,4);
/*!40000 ALTER TABLE `question_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `question_type`
--

LOCK TABLES `question_type` WRITE;
/*!40000 ALTER TABLE `question_type` DISABLE KEYS */;
INSERT  IGNORE INTO `question_type` (`id`, `title`) VALUES (1,'textinput'),(2,'textarea'),(3,'date'),(4,'fileupload'),(5,'application_details'),(6,'empty'),(7,'finance'),(8,'labour'),(9,'overheads'),(10,'materials'),(11,'capital_usage'),(12,'subcontracting_costs'),(13,'travel'),(14,'other_costs'),(15,'your_finance'),(16,'financial_summary'),(17,'other_funding');
/*!40000 ALTER TABLE `question_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `response`
--

LOCK TABLES `response` WRITE;
/*!40000 ALTER TABLE `response` DISABLE KEYS */;
INSERT  IGNORE INTO `response` (`id`, `assessment_confirmation`, `assessment_feedback`, `assessment_score`, `update_date`, `value`, `application_id`, `question_id`, `updated_by_id`) VALUES (1,NULL,NULL,NULL,'2015-09-18 10:33:27','Within the Industry one issue has caused progress in the field to be stifled.  Up until now any advancement has been made by working around this anomaly.  \r\n\r\nWe propose to tackle the situation head on and develop a tool that will circumvent the problem entirely allowing development to advance.\r\n',1,1,1),(4,NULL,NULL,NULL,'2015-09-18 10:35:56','Wastage in our industry can be attributed in no small part to one issue.  To date businesses have been reluctant to tackle that problem and instead worked around it.  That has stifled progress.\r\n\r\nThe end result of our project will be a novel tool to manage the issue and substantially reduce the wastage caused by it.\r\n',1,12,1),(5,NULL,NULL,NULL,'2015-09-18 10:35:16','The amount of money needed to complete this project is large.  \r\n\r\nThe academic phase will require the acquisition of several pieces of lab equipment and the employ of additional graduates to ensure the experiments run smoothly.\r\n\r\nThe Materials and use of the fabricators equipment will account for approximately some of the budget.\r\n\r\nThe time and expertise required across all 3 partners to complete each phase will amount to the lion share of the cost.\r\n',1,15,1),(6,NULL,NULL,NULL,'2015-09-18 10:35:16','Although steps are under way to get this project off the ground, investment by Innovate UK will stimulate Industry leads to supply additional resource funding and use of their facilities as required to advance the progress of this project.  \r\n\r\nUK Investment will ensure that we are able to retain the onward manufacture internally instead of needing to create international deals taking work and profit out of the country.\r\n',1,16,1),(7,NULL,NULL,NULL,'2015-09-18 10:33:27','The issue affects the entire field, the market value of which is significant, and accounts for a percentage of its annual overhead.  A tool to reduce that amount by a significant proportion would be taken up by the entire industry.  \r\n\r\nAt this time no other solutions are available or near market giving us the leading edge on market share.  \r\n\r\nManufacture and development could remain in the United Kingdom with an international export market.\r\n',1,2,1),(8,NULL,NULL,NULL,'2015-09-18 10:33:27','The planned end result of the project would be a fully implementable tool that could be rolled out to the industry. \r\n\r\n Once a fully tested prototype can be demonstrated we can start to take orders and travel to international Industry leaders the exhibit the value of the tool.\r\n',1,3,1),(9,NULL,NULL,NULL,'2015-09-18 10:33:27','The tool will give the consortium the leading edge on the technology as currently no one else is close to considering developing a solution.  \r\n\r\nThe Industry will be able to reduce wastage by a significant degree and pass those savings on to the consumer or reinvest into advancing the field.\r\n  \r\nInside 2 years it is estimated that a very large sum of money can be recouped by using our solution\r\n',1,4,1),(10,NULL,NULL,NULL,'2015-09-18 10:26:33','Our Academic partner has developed a technique to identify the precise cause of the issue and have been able to demonstrate it at a small scale.  Taking this information they will scale up the experiment to a percentage of industrial scale to confirm.  \r\n\r\nWe will take this data and engineer a solution to isolate the problem we will then collude with our fabrication partner to design and prototype a tool to correct the obstacle. \r\n',1,5,1),(11,NULL,NULL,NULL,'2015-09-18 10:26:33','The project is based on Manchester’s novel IP that, for the first time, allows SLM to be conducted without any need for anchors/supports. A search of the literature has established that the advances proposed in this project on which RAMP are based are not available via any commercially available process. In addition, the development and supply of tailored materials by Empire and a dedicated high temperature rig by Ludlow will involve further, critical degrees of innovation that will help to build a portfolio of IP to enhance commercial exploitation. NB. RAMP does not simply reduce stresses by working at higher temperatures; it involves a novel step based on manufacturing engineering and material science that completely eliminates stresses.',1,6,1),(12,NULL,NULL,NULL,'2015-09-18 10:26:33','Three principal risks exist at this stage:\r\n1.        The academic scale up fails to replicate the smaller results either entirely or not to a satisfactory level. In this event the academic partner would begin a controlled scale back to identify if the method needs to be altered during scale up to replicate results.  In the event that the issue can only be identified in the small scale we would build that into our planning for the isolation phase\r\n2.      During the phase to engineer a technique to isolate the issue we anticipate that there are inherent safety issues.  We have consulted an independent firm to ensure that suitable equipment and measures have been put in place to minimise the risk to staff.\r\n3.    Although we are confident that our fabrication partner is the best option for the prototype solution there is an indication that a particular piece of heavy machinery, currently unavailable in the UK will be required.  Our Fabrication partner has already investigated options for accessing this equipment in the Far East and an appropriate budget has been set aside to be used for this purpose if required.\r\n',1,7,1),(13,NULL,NULL,NULL,'2015-09-18 10:26:33','The team consists of:\r\nUs - We have been supplying engineering solutions to the industry for a number of years and have been principal deliverers for various advancements within the field.  We are a suitably sized team capable of developing flexible solutions to a wide variety of clients internationally.\r\n\r\nAcademic – the principal investigator and his team have been studying the issue for a number of years and have qualifications across the spectrum that apply to the field.  Together they have already identified several efficiencies that can be made within the industry.\r\n\r\nThe Fabricators – Are a leading team within complex engineering fabrication.  They have work on multiple high budget engineering solutions and consist of a team of people with many years’ experience in a range of industries\r\n',1,8,1),(15,NULL,NULL,NULL,'2015-09-18 10:35:56','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',1,11,1),(16,NULL,NULL,NULL,'2015-08-08 00:00:00','file.doc',1,14,1),(17,NULL,NULL,NULL,'2015-08-08 00:00:00','file.pdf',1,17,1),(18,NULL,NULL,NULL,'2015-09-18 10:30:39','The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.\r\nIdentification will involve the university testing conditions to determine the exact circumstance of the Issue.\r\nOnce Identification has been assured we will work to Isolate the issue but replicating the circumstances in which it occurs within a laboratory environment.\r\nAfter this we will work with our prototyping partner to create a tool to correct the issue.  Once tested and certified this will be rolled out to mass production.\r\n',1,13,1);
/*!40000 ALTER TABLE `response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT  IGNORE INTO `role` (`id`, `name`) VALUES (1,'leadapplicant'),(2,'collaborator'),(3,'assessor'),(4,'applicant');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `section`
--

LOCK TABLES `section` WRITE;
/*!40000 ALTER TABLE `section` DISABLE KEYS */;
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `name`, `priority`, `competition_id`, `parent_section_id`) VALUES (1,'This section provides context for the application but should not be assessed.',NULL,'Application details',1,1,NULL),(2,'Please indicate whether this application is in scope for this competition.','','Scope',2,1,NULL),(3,NULL,'We will score each of the 4 questions in this section out of 10. Maximum score 40 points.','Your business proposition',3,1,NULL),(4,NULL,'We will score each of the 4 questions in this section out of 10. Maximum score 40 points.','Your approach to the project',4,1,NULL),(5,NULL,'We will score each of the 2 questions in this section out of 10. Maximum score 20 points.','Funding ',5,1,NULL),(6,NULL,'In this section we need to understand the project costs for each partner in your consortium. They mush provide details of their costs within each category according to their role.','Finances',6,1,NULL),(7,NULL,'You can use this section to detail costs for your organisation only. Only your organisation can see this level of detail.','Your finances',1,1,6),(8,NULL,'This is the financial overview of all partners in this collaboration. Each partner should submit their organisations finances in \"your finances\" section. All partners will see this level of detail.','Project financial summary',2,1,6),(9,NULL,NULL,'Labour',1,1,7),(10,NULL,NULL,'Overheads',2,1,7),(11,NULL,NULL,'Materials',3,1,7),(12,NULL,NULL,'Capital usage',4,1,7),(13,NULL,NULL,'Subcontracting costs',5,1,7),(14,NULL,NULL,'Travel and subsistence',6,1,7),(15,NULL,NULL,'Other Costs',7,1,7);
/*!40000 ALTER TABLE `section` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `token`) VALUES (1,'steve.smith@empire.com','image.jpg','Steve Smith','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','123abc'),(2,'jessica.doe@ludlow.co.uk','image2.jpg','Jessica Doe','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','456def'),(3,'paul.plum@gmail.com','image3.jpg','Professor Plum','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','789ghi'),(6,'competitions@innovateuk.gov.uk','image4.jpg','Comp Exec (Competitions)','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','123def'),(7,'finance@innovateuk.gov.uk','image5.jpg','Project Finance Analyst (Finance)','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','123ghi'),(8,'pete.tom@egg.com','image2.jpg','Pete Tom','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27','867def');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (1,4),(2,4),(3,3),(8,4);
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

-- Dump completed on 2015-10-01 10:21:13
