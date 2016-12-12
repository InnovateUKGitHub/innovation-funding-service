SET @programme_competition_id = (SELECT `template_competition_id` FROM `competition_type` WHERE name='Programme');
SET @sector_competition_id = (SELECT `template_competition_id` FROM `competition_type` WHERE name='Sector');

SET @guidance_answer='<p>You may include an appendix of additional information to support the technical approach the project will undertake.</p><p>You may include, for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>';
SET @guidance_title='What should I include in the appendix?';
SET @scope='APPLICATION';
SET @form_input_type_id=4;
SET @included_in_application_summary=1;
SET @description='Appendix';
SET @priority=1;
SET @active=0;

INSERT INTO `form_input` (`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_title`, `guidance_answer`, `priority`, `question_id`, `scope`, `active`)
SELECT  NULL as `word_count`,
        @form_input_type_id as `form_input_type_id`,
        competition_id as `competition_id`,
        @included_in_application_summary as `included_in_application_summary`,
        @description as `description`,
        @guidance_title as `guidance_title`,
        @guidance_answer as `guidance_answer`,
        @priority as `priority`,
        id as `question_id`,
        @scope as `scope`,
        @active as `active`
FROM `question` WHERE (competition_id=@programme_competition_id OR competition_id=@sector_competition_id);



