-- Reset question numbers for Competitions 2 through to 6 after the new 'Application questions' section was added
-- Application details question
UPDATE `question` SET `question_number`=NULL WHERE `id` IN (10, 43, 68, 93, 118, 143);

-- Project summary question
UPDATE `question` SET `question_number`=NULL WHERE `id` IN (11, 44, 69, 94, 119, 144);

-- Public description question
UPDATE `question` SET `question_number`=NULL WHERE `id` IN (12, 45, 70, 95, 120, 145);

-- Scope question
UPDATE `question` SET `question_number`=NULL WHERE `id` IN (13, 46, 71, 96, 121, 146);

-- First of the 'Application questions' which was previously the fifth question now becomes question 1
UPDATE `question` SET `question_number`='1' WHERE `id` IN (14, 47, 72, 97, 122, 147);

-- Insert a new appendix field for Competition 2 Q47 for demonstrating the appendix display of an assessment
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (216, 0, 4, 2, 1, 'Appendix', 'What should I include in the appendix?', '<p>You may include an appendix of additional information to support your response to this question. This appendix may include graphics of a juggling nature.</p><p>The appendix should:</p><ul class="list-bullet"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 5 sides of A4. Longer appendices will only have the first 5 pages assessed</li><li>be less than 1mb in size</li></ul>', 1, 47, 'APPLICATION');

-- Insert applicant responses to the new appendix field of Competition 2 Q47 for applications which are submitted, approved or rejected
-- Application 8
INSERT IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (110, '2016-04-25 13:03:05', 'test1.pdf', 216, 29, 8, 6);

-- Application 9
INSERT IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (111, '2016-04-25 13:06:57', 'test1.pdf', 216, 30, 9, 6);

-- Application 10
INSERT IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (112, '2016-04-25 13:08:45', 'test1.pdf', 216, 31, 10, 6);

-- Application 14
INSERT IGNORE INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (113, '2016-04-25 13:24:05', 'test1.pdf', 216, 35, 14, 6);

-- Populate missing file entry id's of responses to the question "Upload a pdf copy of the Je-S output confirming a status of 'With Council'"
-- Application 14
UPDATE `form_input_response` SET `file_entry_id`=6 WHERE `id`=60;