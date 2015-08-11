-- MySQL dump 10.13  Distrib 5.6.21, for osx10.8 (x86_64)
--
-- Host: localhost    Database: ifs
-- ------------------------------------------------------
-- Server version	5.6.21

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
INSERT  IGNORE INTO `application` (`id`, `name`, `competition`, `application_status_id`) VALUES (1,'Rovel Additive Manufacturing Process',1,1),(2,'Providing sustainable childcare',1,2),(3,'Mobile Phone Data for Logistics Analytics',1,3),(4,'Using natural gas to heat homes',1,4);
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `application_finance`
--

LOCK TABLES `application_finance` WRITE;
/*!40000 ALTER TABLE `application_finance` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_finance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `application_status`
--

LOCK TABLES `application_status` WRITE;
/*!40000 ALTER TABLE `application_status` DISABLE KEYS */;
INSERT IGNORE INTO `application_status` VALUES (1,'created'),(2,'submitted'),(3,'approved'),(4,'rejected');
/*!40000 ALTER TABLE `application_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `capital_usage`
--

LOCK TABLES `capital_usage` WRITE;
/*!40000 ALTER TABLE `capital_usage` DISABLE KEYS */;
/*!40000 ALTER TABLE `capital_usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `competition`
--

LOCK TABLES `competition` WRITE;
/*!40000 ALTER TABLE `competition` DISABLE KEYS */;
INSERT IGNORE INTO `competition` VALUES (1,'Competition name ....','2016-01-25 00:00:00','Innovate UK is to invest up to £9 million in collaborative research and development to stimulate innovation in integrated transport solutions for local authorities. The aim of this competition is to meet user needs by connecting people and/or goods to tra','2015-12-25 00:00:00');
/*!40000 ALTER TABLE `competition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT  IGNORE INTO `question` (`id`, `name`, `competition_id`, `section_id`, `question_type_id`, `character_count`, `description`, `guidance_answer_text`, `guidance_question`, `guidance_question_text`, `guidance_title`, `option_values`) VALUES (1,'1. What is the business opportunity that this project addresses?',1,3,2,0,NULL,NULL,'What should I include in business opportunity?','Explain the business opportunity and the customer need(s) that you have identified, and show how your project will address these. Describe the current challenges this opportunity is designed to address and how you intend to overcome them.',NULL,NULL),(2,'2. What is the size of the market opportunity that this project might open up?',1,3,2,0,NULL,NULL,'What should I include in market opportunity?','Describe the size of the market opportunities that this project might open up, including details of:\n\nCurrent nature of the specific market(s) at which the project is targeted (eg, is it characterised by price competition amongst commoditised suppliers? Is it dominated by a single leading firm?)\nThe dynamics of the market including quantifying its current size, actual and predicted growth rates\nThe projected market share for the project outcome, with justification in the light of any potential competitors\nThe potential to create value-added for the UK and/or the European Economic Area (EEA).\nDescribe and clearly quantify the return on investment that the project could achieve and provide relevant source data references.\n\nWhere possible, provide evidence for your statements about the addressable market for project outcomes and outline your strategy for developing market share.\n\nFor highly innovative projects (see question 6) where the market may be unexplored, explain:\n\nWhat the route to market could or might be\nWhat its size might be\nHow the project will seek to explore the market potential.',NULL,NULL),(3,'3. How will the results of the project be exploited and disseminated?',1,3,2,0,NULL,NULL,'What should I include in proposition exploitation?','List or describe the potential exploitable outputs of the project such as:\n\nProducts or services\nProcesses\nApplications\nThen describe how these outputs will be exploited including, where applicable, the route to market; protection of intellectual property rights; reconfiguration of the value system; changes to business models and business processes and other methods of exploitation and protection.\n\nPLEASE NOTE: Where research organisations are involved in a project and funded for undertaking non-economic activity, we will expect to see evidence in the answer to this question of plans to disseminate their project outputs over a reasonable timescale. The requirement for dissemination of research results intends to secure wider benefit from the higher level of public support given to research organisations. For further information, please see our funding rules.',NULL,NULL),(4,'4. What economic, social and environmental benefits is the project expected to deliver?',1,3,2,0,NULL,NULL,'What should I include in benefits?','Explicitly identify all benefits that will accrue inside and outside of the consortium as a result of the proposed project. Truly sustainable development balances economic growth with social impacts and benefits and the protection of the environment.\n\nBenefits to those outside the consortium and to consortium participants should be considered and you should make a clear distinction between the two.\n\nEconomic – This is the real impact the organisation has on its economic environment. This is not simply traditional corporate accounting profit, and can include cost avoidance, so you should highlight any expected \'spill over\' benefits external to the project, eg, benefits to users (intermediaries and end users), suppliers, the broader industrial markets and the UK economy. The application should identify and quantify where possible the benefit to each of the beneficiaries.\n\nSocial - Quantify any expected social impacts, either positive or negative, on, for example, the quality of life, social inclusion/exclusion, education, public empowerment, health and safety, regulation, diversity, and any expected impact on Government priorities.\n\nEnvironmental – Demonstrate how your project will benefit the natural environment as much as possible or at the least do no harm and curtail environmental impact. For example, this could include careful management of energy consumption and reductions in carbon emissions whilst reducing manufacturing and materials waste, rendering waste less toxic before disposing of it in a safe and legal manner (cradle to grave) or re-manufacturing (cradle to cradle).',NULL,NULL),(5,'5. What technical approach will be adopted and how will the project be managed?',1,4,2,0,'Provide an overview of the technical approach including the main objectives of the work. Describe the main areas of work together with their resource and management requirements',NULL,'What should I include in technical approach?','Describe your technical approach, including the main objectives of the work. Explain how and why your approach is appropriate and how you will ensure that the innovative steps in your project are achievable. Describe rival technologies and alternative R&D strategies and describe why your proposed approach will offer a better outcome.\n\nYou may attach a project plan as an appendix to the question to assist your response.',NULL,NULL),(6,'6. What is innovative about this project?',1,4,2,0,'Identify the extent to which the project is innovative both commercially and technically',NULL,'What should I include in understanding innovation?','In evaluating this section assessors will consider these questions:\n\nDoes it push boundaries over and beyond current leading-edge world science and technology?\nIs it looking to apply existing technologies in new areas?\nHighlight and explain the timeliness and novelty of the research aspects of the project in an industrial and/or academic context.\n\nDescribe any evidence you have to substantiate your belief that the intended work is innovative. This could include the results of patent searches, competitor analyses, literature surveys etc. If applicable, you should also outline your own background intellectual property rights, as related to the project.',NULL,NULL),(7,'7. What are the risks (technical, commercial and environmental) to project success? What is the project\'s risk management strategy?',1,4,2,0,'Innovate UK recognises that projects of this type are inherently risky, but seeks assurance that the projects it funds have adequate arrangements for managing this risk',NULL,'What should I include in project risks?','Focus on the arrangements for managing and mitigating risk as follows:\n\nIdentify the key risks and uncertainties of the project and provide a detailed risk analysis for the project content and approach, including the technical, commercial, managerial and environmental risks as well as other uncertainties (eg, ethical issues) associated with the project. The main risks should then be rated as High/Medium/Low (H/M/L)\nState how the project would mitigate these key risks. You should address all significant and relevant risks and their mitigation\nIdentify key project management tools and mechanisms that will be implemented to provide confidence that sufficient control will be in place to minimise operational risk and, therefore, promote successful project delivery. This should include the arrangements for managing the project team and its partners.',NULL,NULL),(8,'8. Does the project team have the right skills and experience and access to facilities to deliver the identified benefits?',1,4,2,0,'Describe the track record of the project team members in undertaking and exploiting the results of research and development projects, to show your capability to develop and exploit the technology',NULL,'What should I include in project skills?','In evaluating this, the assessors will consider whether:\n\nthe project team has the right available mix of skills and experience to deliver the project successfully\nthe project team\'s formation objectives are clear and if it would have been formed without Innovate UK investment\nif a consortium, if there is additional benefit demonstrated from the collaboration, for example, increased knowledge transfer; and if the consortium is greater than the sum of its parts – how the organisations working together will achieve more than if they were working individually',NULL,NULL),(9,'Project title',1,1,1,255,'Enter the full title of the project',NULL,NULL,NULL,NULL,NULL),(10,'Project timescales',1,1,3,0,'Enter the target start date and its planned duration. These are indicative at this stage and are not guaranteed\nProject start date\nFor example: 31 12 2015',NULL,NULL,NULL,NULL,NULL),(11,'Project summary',1,1,2,1000,'Please provide a short summary of the content and objectives of the project including what is innovative about it',NULL,'What should I include in project summary?','This is an opportunity to provide a short summary of the key objectives and focus areas of the project. It is important that this summary is presented in reference to the main outline of the project, with sufficient information to provide a clear understanding of the overall vision of the project and its innovative nature.\n\nThis summary is not scored, but provides an introduction of your proposal for the benefit of the assessors.',NULL,NULL),(12,'Public description',1,1,2,1000,'If your application is successful, Innovate UK will publish the following brief description of your proposal. Provision of this description is mandatory but will not be assessed',NULL,'What should I include in project public description?','To comply with Government practice on openness and transparency of public-funded activities, Innovate UK has to publish information relating to funded projects. Please provide a short description of your proposal in a way that will be comprehensible to the general public. Do not include any commercially confidential information, for example intellectual property or patent details, in this summary.\n\nWhilst this section is not assessed, provision of this public description is mandatory. Funding will not be provided to successful projects without this',NULL,NULL),(13,'How does your application align with the specific competition scope?',1,2,2,1000,'Note that if your application does not meet the specific requirements of this question your application will be rejected',NULL,'What should I include in project scope?','If the majority of the assessors consider that the following answer to the Scope Gateway question is \'No\', then the application will not be approved for funding. Guidance on the \'Gateway Question: Scope\' is therefore critically important and is provided below.\n\nAll applications must align with the specific competition scope criteria as described in the relevant competition Brief.\n\nNote: To demonstrate alignment, you need to show that a clear majority of the project\'s objectives and activities are aligned with the specific competition. In forming their judgment on this, the assessors will also consider whether the application addresses the research objectives and topics it claims to. It is important, therefore, for you to understand fully the background, challenge and scope of the competition, as outlined in the Competition Brief.\n\nFull Stage Applications:\n\nFor a full stage application, if feedback has already been given on this section, be careful not to write yourself out of scope.\nOnly improve this section in the full stage if the feedback requested further clarity and/or information on the response to meeting the objectives of the scope.',NULL,NULL),(14,' ',1,4,4,0,'Additional information can be provided to support your answer to this question in an appendix (optional).',NULL,'What should I include in Question 5 appendix?','It is important to note that these are intended to contain supporting information and not substantive elements of answers to the application form questions. Do not, therefore, use the appendices as an overflow to this question.\n\nThis appendix should be used to support Question 5, so you may therefore choose to include a Gantt chart of project management structure.\n\nIn order that assessors can open and read the appendices, each appendix must:\n\nBe submitted in Portable Document Format (.pdf)\nBe legible at 100% zoom/magnification\nDisplay prominently the \'Project title\' as entered on page 1 of the application form\nBe a maximum of 2 sides of A4\nIf you submit appendices longer than specified below, they will be truncated and the excess discarded.\n\nAppendices may be printed or photocopied in black and white, so colour should not be used as the sole method of conveying important information.',NULL,NULL),(15,'What is the financial commitment required for your project?',1,5,2,1000,'Supporting information and explanation for project costs should be provided in this section of the form. Indicate the anticipated project cost making clear the level of contribution from any project participants and the level of funding required from Innovate UK.',NULL,'What should I include in financial commitment?','It must be consistent with the category of research & development being undertaken within each work package. Please see the guidance section of our website for further details on funding: https://interact.innovateuk.org/-/funding-rules **indicate where this will live\n\nImportant: If the project spans more than one type of funding (eg, because significant work packages are in both fundamental and industrial research), you must describe and justify the breakdown of costs between them within the answer to this question.\n\nIn evaluating this the assessors will consider the following questions:\n\nIs the budget realistic for the scale and complexity of the project?\nDoes the financial support required from Innovate UK fit within the limits set by the specific competition?\nIs a financial commitment from other sources demonstrated for the balance of the project costs?\nHas a realistic budget breakdown been provided?\nHave any work package breakdowns been described and justified adequately?\nDetailed guidance on eligible and ineligible project costs is provided on our website: https://interact.innovateuk.org/-/project-costs ** indicate where this will live\n\nPlease note: For collaborations involving Research Organisations, the costs of PhD Research Students are no longer eligible to be included in project costs. This is in line with current Research Council guidelines. Please refer to the guidance via the above link for further details\n\nEnsure that all key points relating to the finances of your project that you wish the assessors to consider are included in the main body of your application form, or in the relevant appendix, as these are the key documents used within the assessment process.',NULL,NULL),(16,'How does financial support from Innovate UK and its funding partners add value?',1,5,2,1000,' ',NULL,'What should I include in financial commitment?','Describe how successful delivery of your project will allow your project team partners to spend more money on research and development in the UK than if this project does not proceed.\n\nJustify why you are unable to fund the project yourself or from commercial sources. Explain the difference this funding will make to your project (for example it may lower the risk for you or mean you can get to market more quickly) and why this will benefit the UK.',NULL,NULL),(17,' ',1,4,4,0,'Additional information can be provided to support your answer to this question in an appendix (optional).',NULL,'What should I include in Question 6 appendix?','It is important to note that these are intended to contain supporting information and not substantive elements of answers to the application form questions. Do not, therefore, use the appendices as an overflow to this question.\n\nUse this appendix to support Question 6. You may include details of evidence for innovation.\n\nIn order that assessors can open and read the appendices, each appendix must:\n\nBe submitted in Portable Document Format (.pdf)\nBe legible at 100% zoom/magnification\nDisplay prominently the \'Project title\' as entered on page 1 of the application form\nBe a maximum of 2 sides of A4\nIf you submit appendices longer than specified below, they will be truncated and the excess discarded.\n\nAppendices may be printed or photocopied in black and white, so colour should not be used as the sole method of conveying important information.',NULL,NULL),(18,' ',1,4,4,0,'Additional information can be provided to support your answer to this question in an appendix (optional).',NULL,'What should I include in Question 8 appendix?','It is important to note that these are intended to contain supporting information and not substantive elements of answers to the application form questions. Do not, therefore, use the appendices as an overflow to this question.\n\nUse this appendix to provide details of the specific expertise and track record of each project partner and each subcontractor to address Question 8 of the application form. Academic collaborators may wish to refer to their research standing.\n\nIn order that assessors can open and read the appendices, each appendix must:\n\nBe submitted in Portable Document Format (.pdf)\nBe legible at 100% zoom/magnification\nDisplay prominently the \'Project title\' as entered on page 1 of the application form\nInclude a maximum of half a side of A4 for each partner and subcontractor\nIf you submit appendices longer than specified below, they will be truncated and the excess discarded.\n\nAppendices may be printed or photocopied in black and white, so colour should not be used as the sole method of conveying important information.',NULL,NULL);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `question_type`
--

LOCK TABLES `question_type` WRITE;
/*!40000 ALTER TABLE `question_type` DISABLE KEYS */;
INSERT IGNORE INTO `question_type` VALUES (1,'textinput'),(2,'textarea'),(3,'date'),(4,'fileupload');
/*!40000 ALTER TABLE `question_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `response`
--

LOCK TABLES `response` WRITE;
/*!40000 ALTER TABLE `response` DISABLE KEYS */;
INSERT  IGNORE INTO `response` (`id`, `date`, `marked_as_complete`, `question_id`, `user_application_role_id`, `value`) VALUES (1,NULL,'\0',1,1,'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec non magna leo. Aenean gravida dolor ut dolor tincidunt, non porta lectus pharetra. Suspendisse laoreet ante feugiat velit condimentum dictum eu non metus. Nam efficitur volutpat finibus. Aliquam eros nibh, pharetra eu vulputate a, viverra sit amet purus. Duis nibh orci, tincidunt id accumsan eu, dignissim id erat. Nullam libero nibh, aliquet sit amet malesuada eget, luctus vitae purus. Suspendisse sapien augue, viverra vel viverra eu, dapibus vitae felis. Nullam sodales euismod porttitor.fff f f fff f ffff ');
/*!40000 ALTER TABLE `response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT IGNORE INTO `role` VALUES (1,'leadapplicant'),(2,'collaborator');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `section`
--

LOCK TABLES `section` WRITE;
/*!40000 ALTER TABLE `section` DISABLE KEYS */;
INSERT IGNORE INTO `section` VALUES (1,'Application details',1,NULL),(2,'Scope (Gateway question)',1,NULL),(3,'Business proposition (Q1 - Q4)',1,NULL),(4,'Project approach (Q5 - Q8)',1,NULL),(5,'Funding (Q9 - Q10)',1,NULL),(6,'Finances',1,NULL);
/*!40000 ALTER TABLE `section` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `subcontractor`
--

LOCK TABLES `subcontractor` WRITE;
/*!40000 ALTER TABLE `subcontractor` DISABLE KEYS */;
/*!40000 ALTER TABLE `subcontractor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `travel_cost`
--

LOCK TABLES `travel_cost` WRITE;
/*!40000 ALTER TABLE `travel_cost` DISABLE KEYS */;
/*!40000 ALTER TABLE `travel_cost` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT  IGNORE INTO `user` (`id`, `image_url`, `name`, `token`, `email`, `password`) VALUES (1,'image.jpg','Steve Smith (Lead Applicant)','123abc','applicant@innovateuk.org','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27'),(2,'image2.jpg','Jessica Doe (Collaborator)','456def','collaborator@innovateuk.org','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27'),(3,'image3.jpg','Professor Plum (Assessor)','789ghi','assessor@innovateuk.org','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27'),(6,'image4.jpg','Comp Exec (Competitions)','123def','competitions@innovateuk.org','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27'),(7,'image5.jpg','Project Finance Analyst (Finance)','123ghi','finance@innovateuk.org','67bf8182199a451fbae14c67711f2f05eef8e2f464e4bd51f3b810111a0033dc64ef696e02cf8f27');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_application_role`
--

LOCK TABLES `user_application_role` WRITE;
/*!40000 ALTER TABLE `user_application_role` DISABLE KEYS */;
INSERT IGNORE INTO `user_application_role` VALUES (1,1,1,1,1),(2,2,1,1,1),(3,3,1,1,1),(4,4,1,1,1),(5,1,1,2,2),(6,4,1,2,2);
/*!40000 ALTER TABLE `user_application_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-08-11 14:17:55
