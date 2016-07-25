ALTER TABLE `form_input`
    ADD COLUMN `priority` int(11) NOT NULL,
    ADD COLUMN `question_id` bigint(20) DEFAULT NULL,
    ADD CONSTRAINT `form_input_to_question_fk` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
;

UPDATE
`form_input`
INNER JOIN `question_form_input`
    ON (form_input.id = question_form_input.form_input_id)
SET
form_input.priority = question_form_input.priority,
form_input.question_id = question_form_input.question_id
;

ALTER TABLE `form_input` MODIFY `question_id` bigint(20) NOT NULL;

DROP TABLE `question_form_input`;