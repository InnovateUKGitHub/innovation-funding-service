-- Create Assessor Form Inputs for each Question in the 'Project details' and 'Application questions' sections where appropriate

-- Competition 1
-- Q1
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (43,NULL,23,1,0,'Question score',NULL,NULL,0,1,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (44,100,2,1,0,'Feedback','Guidance for assessing business opportunity','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The applicants have a very clear understanding of the business opportunity and the problems that must be overcome to enable successful exploitation. The project is well aligned with these needs.<tr><th scope="row" style="width: 50px">7-8<td>The applicants have a good idea of the potential market and opportunities. The needs of the customer are central to the project''s objectives.<tr><th scope="row" style="width: 50px">5-6<td>The business opportunity is plausible but not clearly expressed in terms of customer needs.<tr><th scope="row" style="width: 50px">3-4<td>The business opportunity is unrealistic or poorly defined. The customer''s true needs are not well understood and are not linked to the project''s objectives.<tr><th scope="row" style="width: 50px">1-2<td>There is little or no business drive to the project. The results are not relevant to the target customers or no customer interests are provided.</table>',1,1,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (43,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (44,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (44,3);

-- Q2
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (45,NULL,23,1,0,'Question score',NULL,NULL,0,2,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (46,100,2,1,0,'Feedback','Guidance for assessing market opportunity','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The market size and dynamics are quantified clearly and to sufficient resolution to be relevant to the project. The market is clearly well understood. The return on investment is clearly stated, quantified and realistic.<tr><th scope="row" style="width: 50px">7-8<td>The market size and dynamics are described with some quantification relevant to the project. Market understanding is acceptable and the return on investment is achievable.<tr><th scope="row" style="width: 50px">5-6<td>The market size and dynamics are understood but poorly quantified or stated at a level not really relevant for the project. Return on investment is plausible or badly defined.<tr><th scope="row" style="width: 50px">3-4<td>The market size is not quantified but there is some understanding. Return on investment is ill defined or unrealistic.<tr><th scope="row" style="width: 50px">1-2<td>The market is not well defined or is wrong. No sensible return on investment is provided.</table>',1,2,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (45,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (46,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (46,3);

-- Q3
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (47,NULL,23,1,0,'Question score',NULL,NULL,0,3,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (48,100,2,1,0,'Feedback','Guidance for assessing project exploitation','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The principle exploitable outputs of the project are identified together with clear and achievable exploitation methods. Dissemination opportunities are also identified and appropriate.<tr><th scope="row" style="width: 50px">7-8<td>The main exploitable output of the project is identified and a realistic method defined. Some dissemination is also explained.<tr><th scope="row" style="width: 50px">5-6<td>An exploitation method is defined but lacking in detail or is only just feasible. Dissemination is mentioned.<tr><th scope="row" style="width: 50px">3-4<td>The exploitation and dissemination methods described are unrealistic or ill-defined.<tr><th scope="row" style="width: 50px">1-2<td>The exploitation method is missing or un-feasible and unlikely to succeed.</table>',1,3,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (47,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (48,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (48,3);

-- Q4
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (49,NULL,23,1,0,'Question score',NULL,NULL,0,4,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (50,100,2,1,0,'Feedback','Guidance for assessing project benefits','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>Inside and outside benefits are well defined, realistic and of significantly positive economic, environmental or social impact. Routes to exploit these benefits are also provided.<tr><th scope="row" style="width: 50px">7-8<td>Some positive outside benefits are defined and are realistic. Methods of addressing these opportunities are described.<tr><th scope="row" style="width: 50px">5-6<td>Some positive outside benefits are described but the methods to exploit these are not obvious. Or the project is likely to have a negative impact but some mitigation or a balance against the internal benefits is proposed.<tr><th scope="row" style="width: 50px">3-4<td>The project has no outside benefits or is potentially damaging to other stakeholders. No mitigation or exploitation is suggested.<tr><th scope="row" style="width: 50px">1-2<td>The project is damaging to other stakeholders with no realistic mitigation or balance described.</table>',1,4,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (49,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (50,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (50,3);

-- Q5
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (51,NULL,23,1,0,'Question score',NULL,NULL,0,5,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (52,100,2,1,0,'Feedback','Guidance for assessing technical approach','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The project plan is fully described and complete with milestones and timeframes. The plan is realistic and should meet the objectives of the project.<tr><th scope="row" style="width: 50px">7-8<td>The plan is well described and complete. There is a reasonable chance that it will meet the objectives of the project.<tr><th scope="row" style="width: 50px">5-6<td>The plan is not completely described or there may be deficiencies in some aspects. More work will be required before the plan can be said to be realistic.<tr><th scope="row" style="width: 50px">3-4<td>The plan has serious deficiencies or major missing aspects. The plan has little chance of meeting the objectives of the project.<tr><th scope="row" style="width: 50px">1-2<td>The plan is totally unrealistic or fails to meet the objectives of the project.</table>',1,5,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (51,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (52,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (52,3);

-- Q6
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (53,NULL,23,1,0,'Question score',NULL,NULL,0,6,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (54,100,2,1,0,'Feedback','Guidance for assessing innovation','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The project is significantly innovative either commercially or technically and will make a substantial contribution to the field. Solid evidence is presented to substantiate the level of innovation.<tr><th scope="row" style="width: 50px">7-8<td>The project will be innovative and relevant to the market. There is high confidence that there is freedom to operate.<tr><th scope="row" style="width: 50px">5-6<td>The project is innovative but there is a lack of presented evidence as to the freedom to operate.<tr><th scope="row" style="width: 50px">3-4<td>The project lacks sufficient innovation both technically and commercially.<tr><th scope="row" style="width: 50px">1-2<td>The project is either not innovative or there is no exploitable route due to previous IP.</table>',1,6,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (53,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (54,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (54,3);

-- Q7
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (55,NULL,23,1,0,'Question score',NULL,NULL,0,7,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (56,100,2,1,0,'Feedback','Guidance for assessing risks','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>A thorough risk analysis has been presented across all 3 risk categories. The mitigation and risk management strategies proposed are also appropriate and professional.<tr><th scope="row" style="width: 50px">7-8<td>A good risk analysis has been carried out and the management methods and mitigation strategies proposed are realistic.<tr><th scope="row" style="width: 50px">5-6<td>Most major risks have been identified but there are some gaps or the mitigation and management is insufficient to properly control the risks.<tr><th scope="row" style="width: 50px">3-4<td>The risk analysis is poor or misses major areas of risk. The mitigation and management is poor.<tr><th scope="row" style="width: 50px">1-2<td>The risk analysis is superficial with minimal mitigation or management suggested.</table>',1,7,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (55,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (56,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (56,3);

-- Q8
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (57,NULL,23,1,0,'Question score',NULL,NULL,0,8,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (58,100,2,1,0,'Feedback','Guidance for assessing team skills','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The consortium is ideally placed to carry out the project AND exploit the results. The skills are well balanced and the partners likely to form a strong consortium with good knowledge transfer.<tr><th scope="row" style="width: 50px">7-8<td>The consortium is strong and contains all the required skills and experience. The consortium is likely to work well.<tr><th scope="row" style="width: 50px">5-6<td>The consortium has most of the required skills and experience but there are a few gaps. The consortium will need to work hard to maintain a good working relationship.<tr><th scope="row" style="width: 50px">3-4<td>There are significant gaps in the consortium or the formation objectives are unclear. There could be some passengers or the balance of work/commitment is poor.<tr><th scope="row" style="width: 50px">1-2<td>The consortium is not capable of either carrying out the project or exploiting the results.</table>',1,8,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (57,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (58,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (58,3);

-- Q13
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (59,NULL,21,1,0,'Please select the research category for this project',NULL,NULL,0,13,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (60,NULL,22,1,0,'Is the application in scope?',NULL,NULL,1,13,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (61,100,2,1,0,'Feedback','Guidance for assessing scope','<p>Your answer should be based upon the following:<table><tr><th scope="row">YES<td><p>The application contains the following:<ul><li>Is the consortia business led?<li>Are there two or more partners to the collaboration?<li>Does it meet the scope of the competition as defined in the competition brief?</ul><tr><th scope="row">NO<td><p>One or more of the above requirements have not been satisfied.</table><p class=extra-margin>Your assessment of the project scope should be based upon the <a href=#>competition brief</a>.',2,13,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (59,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (60,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (61,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (61,3);

-- Q15
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (62,NULL,23,1,0,'Question score',NULL,NULL,0,15,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (63,100,2,1,0,'Feedback','Guidance for assessing financial commitment','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The project costs are entirely appropriate. Any mix of research and development types (eg industrial research with some work packages of experimental development) is justified and costed correctly.<tr><th scope="row" style="width: 50px">7-8<td>The project costs are appropriate and should be sufficient to successfully complete the project. Any mix of research and development types is justified and costed correctly.<tr><th scope="row" style="width: 50px">5-6<td>The project costs and justifications are broadly correct but there are some concerns. Any mix of research and development types is just about acceptable.<tr><th scope="row" style="width: 50px">3-4<td>The costs are either too high or too low to carry out the project. Poor justification is provided for any research and development type mix.<tr><th scope="row" style="width: 50px">1-2<td>The costs are not appropriate or justified. Any mix of research and development type is not justified.</table>',1,15,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (62,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (63,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (63,3);

-- Q16
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (64,NULL,23,1,0,'Question score',NULL,NULL,0,16,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (65,100,2,1,0,'Feedback','Guidance for assessing added value','<p>Your score should be based upon the following:<table><tr><th scope="row" style="width: 50px">9-10<td>The project will significantly increase the industrial partners'' R&D spend during the project and afterwards. The additionality arguments are very strong and believable.<tr><th scope="row" style="width: 50px">7-8<td>The project will increase the industrial partners'' commitment to R&D. The additionality arguments are good and justified.<tr><th scope="row" style="width: 50px">5-6<td>The project will improve the industrial partners'' commitment to R&D. The additionality arguments are just about acceptable.<tr><th scope="row" style="width: 50px">3-4<td>There is not likely to be any improvement to the industrial partner''s commitment to R&D. The additionality arguments are poor or not sufficiently justified.<tr><th scope="row" style="width: 50px">1-2<td>The work should be funded internally and does not deserve state funding.</table>',1,16,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (64,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (65,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (65,3);

-- Add Assessor responses for Competition 1, Assessment 1 (Submitted)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (1,'8',1,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (2,'This is the feedback from Professor Plum for Business opportunity.',1,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (3,'8',1,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (4,'This is the feedback from Professor Plum for Potential market.',1,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (5,'7',1,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (6,'This is the feedback from Professor Plum for Project exploitation.',1,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (7,'6',1,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (8,'This is the feedback from Professor Plum for Economic benefit.',1,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (9,'9',1,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (10,'This is the feedback from Professor Plum for Technical approach.',1,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (11,'7',1,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (12,'This is the feedback from Professor Plum for Innovation.',1,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (13,'3',1,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (14,'This is the feedback from Professor Plum for Risks.',1,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (15,'10',1,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (16,'This is the feedback from Professor Plum for Project team.',1,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (17,'Industrial research',1,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (18,'1',1,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (19,'This is the feedback from Professor Plum for Scope.',1,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (20,'8',1,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (21,'This is the feedback from Professor Plum for Funding.',1,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (22,'6',1,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (23,'This is the feedback from Professor Plum for Adding value.',1,65,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 2 (Open)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (24,'3',2,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (25,'This is the feedback from Professor Plum for Business opportunity.',2,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (26,'7',2,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (27,'This is the feedback from Professor Plum for Potential market.',2,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (28,'3',2,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (29,'This is the feedback from Professor Plum for Project exploitation.',2,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (30,'3',2,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (31,'This is the feedback from Professor Plum for Economic benefit.',2,50,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (32,'5',2,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (33,'This is the feedback from Professor Plum for Risks.',2,56,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (34,'Technical feasibility',2,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (35,'1',2,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (36,'This is the feedback from Professor Plum for Scope.',2,61,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 3 (Assessed)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (37,'1',3,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (38,'This is the feedback from Professor Plum for Business opportunity.',3,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (39,'2',3,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (40,'This is the feedback from Professor Plum for Potential market.',3,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (41,'3',3,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (42,'This is the feedback from Professor Plum for Project exploitation.',3,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (43,'2',3,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (44,'This is the feedback from Professor Plum for Economic benefit.',3,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (45,'3',3,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (46,'This is the feedback from Professor Plum for Technical approach.',3,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (47,'4',3,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (48,'This is the feedback from Professor Plum for Innovation.',3,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (49,'1',3,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (50,'This is the feedback from Professor Plum for Risks.',3,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (51,'2',3,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (52,'This is the feedback from Professor Plum for Project team.',3,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (53,'Experimental development',3,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (54,'1',3,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (55,'This is the feedback from Professor Plum for Scope.',3,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (56,'2',3,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (57,'This is the feedback from Professor Plum for Funding.',3,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (58,'1',3,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (59,'This is the feedback from Professor Plum for Adding value.',3,65,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 5 (Submitted)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (60,'6',5,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (61,'This is the feedback from Felix Wilson for Business opportunity.',5,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (62,'9',5,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (63,'This is the feedback from Felix Wilson for Potential market.',5,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (64,'4',5,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (65,'This is the feedback from Felix Wilson for Project exploitation.',5,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (66,'9',5,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (67,'This is the feedback from Felix Wilson for Economic benefit.',5,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (68,'9',5,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (69,'This is the feedback from Felix Wilson for Technical approach.',5,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (70,'7',5,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (71,'This is the feedback from Felix Wilson for Innovation.',5,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (72,'2',5,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (73,'This is the feedback from Felix Wilson for Risks.',5,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (74,'9',5,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (75,'This is the feedback from Felix Wilson for Project team.',5,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (76,'Technical feasibility',5,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (77,'1',5,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (78,'This is the feedback from Felix Wilson for Scope.',5,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (79,'9',5,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (80,'This is the feedback from Felix Wilson for Funding.',5,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (81,'8',5,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (82,'This is the feedback from Felix Wilson for Adding value.',5,65,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 6 (Open)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (83,'7',6,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (84,'This is the feedback from Felix Wilson for Business opportunity.',6,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (85,'5',6,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (86,'This is the feedback from Felix Wilson for Potential market.',6,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (87,'4',6,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (88,'This is the feedback from Felix Wilson for Project exploitation.',6,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (89,'5',6,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (90,'This is the feedback from Felix Wilson for Economic benefit.',6,50,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (91,'8',6,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (92,'This is the feedback from Felix Wilson for Risks.',6,56,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (93,'Industrial research',6,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (94,'1',6,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (95,'This is the feedback from Felix Wilson for Scope.',6,61,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 7 (Assessed)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (96,'4',7,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (97,'This is the feedback from Felix Wilson for Business opportunity.',7,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (98,'3',7,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (99,'This is the feedback from Professor Plum for Potential market.',7,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (100,'1',7,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (101,'This is the feedback from Felix Wilson for Project exploitation.',7,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (102,'5',7,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (103,'This is the feedback from Felix Wilson for Economic benefit.',7,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (104,'1',7,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (105,'This is the feedback from Felix Wilson for Technical approach.',7,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (106,'1',7,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (107,'This is the feedback from Felix Wilson for Innovation.',7,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (108,'2',7,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (109,'This is the feedback from Felix Wilson for Risks.',7,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (110,'2',7,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (111,'This is the feedback from Felix Wilson for Project team.',7,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (112,'Experimental development',7,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (113,'1',7,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (114,'This is the feedback from Felix Wilson for Scope.',7,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (115,'1',7,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (116,'This is the feedback from Felix Wilson for Funding.',7,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (117,'1',7,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (118,'This is the feedback from Felix Wilson for Adding value.',7,65,'2016-07-15 11:41:36');

-- Set the maximum assessor score for Questions
-- Competition 1
UPDATE `question` SET `assessor_maximum_score`=10 WHERE `id` IN (1,2,3,4,5,6,7,8,15,16);

-- Add Assessor responses for Competition 1, Assessment 1 (Submitted)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (1,'8',1,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (2,'This is the feedback from Professor Plum for Business opportunity.',1,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (3,'8',1,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (4,'This is the feedback from Professor Plum for Potential market.',1,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (5,'7',1,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (6,'This is the feedback from Professor Plum for Project exploitation.',1,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (7,'6',1,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (8,'This is the feedback from Professor Plum for Economic benefit.',1,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (9,'9',1,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (10,'This is the feedback from Professor Plum for Technical approach.',1,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (11,'7',1,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (12,'This is the feedback from Professor Plum for Innovation.',1,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (13,'3',1,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (14,'This is the feedback from Professor Plum for Risks.',1,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (15,'10',1,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (16,'This is the feedback from Professor Plum for Project team.',1,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (17,'Industrial research',1,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (18,'1',1,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (19,'This is the feedback from Professor Plum for Scope.',1,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (20,'8',1,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (21,'This is the feedback from Professor Plum for Funding.',1,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (22,'6',1,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (23,'This is the feedback from Professor Plum for Adding value.',1,65,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 2 (Open)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (24,'3',2,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (25,'This is the feedback from Professor Plum for Business opportunity.',2,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (26,'7',2,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (27,'This is the feedback from Professor Plum for Potential market.',2,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (28,'3',2,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (29,'This is the feedback from Professor Plum for Project exploitation.',2,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (30,'3',2,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (31,'This is the feedback from Professor Plum for Economic benefit.',2,50,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (32,'5',2,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (33,'This is the feedback from Professor Plum for Risks.',2,56,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (34,'Technical feasibility',2,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (35,'1',2,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (36,'This is the feedback from Professor Plum for Scope.',2,61,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 3 (Assessed)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (37,'1',3,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (38,'This is the feedback from Professor Plum for Business opportunity.',3,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (39,'2',3,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (40,'This is the feedback from Professor Plum for Potential market.',3,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (41,'3',3,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (42,'This is the feedback from Professor Plum for Project exploitation.',3,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (43,'2',3,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (44,'This is the feedback from Professor Plum for Economic benefit.',3,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (45,'3',3,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (46,'This is the feedback from Professor Plum for Technical approach.',3,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (47,'4',3,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (48,'This is the feedback from Professor Plum for Innovation.',3,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (49,'1',3,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (50,'This is the feedback from Professor Plum for Risks.',3,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (51,'2',3,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (52,'This is the feedback from Professor Plum for Project team.',3,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (53,'Experimental development',3,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (54,'1',3,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (55,'This is the feedback from Professor Plum for Scope.',3,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (56,'2',3,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (57,'This is the feedback from Professor Plum for Funding.',3,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (58,'1',3,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (59,'This is the feedback from Professor Plum for Adding value.',3,65,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 5 (Submitted)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (60,'6',5,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (61,'This is the feedback from Felix Wilson for Business opportunity.',5,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (62,'9',5,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (63,'This is the feedback from Felix Wilson for Potential market.',5,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (64,'4',5,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (65,'This is the feedback from Felix Wilson for Project exploitation.',5,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (66,'9',5,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (67,'This is the feedback from Felix Wilson for Economic benefit.',5,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (68,'9',5,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (69,'This is the feedback from Felix Wilson for Technical approach.',5,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (70,'7',5,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (71,'This is the feedback from Felix Wilson for Innovation.',5,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (72,'2',5,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (73,'This is the feedback from Felix Wilson for Risks.',5,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (74,'9',5,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (75,'This is the feedback from Felix Wilson for Project team.',5,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (76,'Technical feasibility',5,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (77,'1',5,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (78,'This is the feedback from Felix Wilson for Scope.',5,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (79,'9',5,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (80,'This is the feedback from Felix Wilson for Funding.',5,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (81,'8',5,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (82,'This is the feedback from Felix Wilson for Adding value.',5,65,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 6 (Open)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (83,'7',6,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (84,'This is the feedback from Felix Wilson for Business opportunity.',6,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (85,'5',6,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (86,'This is the feedback from Felix Wilson for Potential market.',6,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (87,'4',6,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (88,'This is the feedback from Felix Wilson for Project exploitation.',6,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (89,'5',6,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (90,'This is the feedback from Felix Wilson for Economic benefit.',6,50,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (91,'8',6,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (92,'This is the feedback from Felix Wilson for Risks.',6,56,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (93,'Industrial research',6,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (94,'1',6,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (95,'This is the feedback from Felix Wilson for Scope.',6,61,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 7 (Assessed)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (96,'4',7,43,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (97,'This is the feedback from Felix Wilson for Business opportunity.',7,44,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (98,'3',7,45,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (99,'This is the feedback from Professor Plum for Potential market.',7,46,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (100,'1',7,47,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (101,'This is the feedback from Felix Wilson for Project exploitation.',7,48,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (102,'5',7,49,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (103,'This is the feedback from Felix Wilson for Economic benefit.',7,50,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (104,'1',7,51,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (105,'This is the feedback from Felix Wilson for Technical approach.',7,52,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (106,'1',7,53,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (107,'This is the feedback from Felix Wilson for Innovation.',7,54,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (108,'2',7,55,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (109,'This is the feedback from Felix Wilson for Risks.',7,56,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (110,'2',7,57,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (111,'This is the feedback from Felix Wilson for Project team.',7,58,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (112,'Experimental development',7,59,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (113,'1',7,60,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (114,'This is the feedback from Felix Wilson for Scope.',7,61,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (115,'1',7,62,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (116,'This is the feedback from Felix Wilson for Funding.',7,63,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (117,'1',7,64,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (118,'This is the feedback from Felix Wilson for Adding value.',7,65,'2016-07-15 11:41:36');

-- Set the maximum assessor score for Questions
-- Competition 1
UPDATE `question` SET `assessor_maximum_score`=10 WHERE `id` IN (1,2,3,4,5,6,7,8,15,16);