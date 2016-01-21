INSERT INTO `question` (`id`, `assign_enabled`, `description`, `mark_as_completed_enabled`, `multiple_statuses`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES (41, 0, '<h2 class=\"heading-medium\">Funding rules for this competition</h2> <p>We will fund projects between &pound;200,000 and &pound;1 million. We may consider projects with costs outside of this range. We expect projects to last between 12 and 24 months.</p> <p>To support our business focus, Innovate UK aims to optimise the level of funding business receives whilst recognising the importance of the contribution of research organisations to R&amp;D projects. We require, therefore, the following levels of participation:</p> <ul class=\"list-bullet\"> <li>at least 70% of the total eligible project costs are incurred by commercial organisations and</li> <li>a maximum of 30% of total eligible project costs is available to research participants. Where there is more than one research participant, this maximum will be shared between them.</li> </ul>', 0, 0, 0, 0, '16', '1', '8');
INSERT INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`) VALUES ('41', '0', '6', '1', '1');
INSERT INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('41', '41', '0');
UPDATE `question` SET `priority`='16' WHERE `id`='41';
UPDATE `question` SET `priority`='17' WHERE `id`='36';
UPDATE `question` SET `priority`='18' WHERE `id`='40';
UPDATE `question` SET `priority`='19' WHERE `id`='38';
UPDATE `question` SET `priority`='20' WHERE `id`='35';
UPDATE `competition` SET `max_research_ratio`='30' WHERE `id`='1';
