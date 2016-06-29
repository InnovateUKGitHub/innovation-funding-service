ALTER TABLE `question`
    ADD COLUMN `assessor_guidance_answer` varchar(255) DEFAULT NULL,
    ADD COLUMN `assessor_guidance_question` varchar(255) DEFAULT NULL
;