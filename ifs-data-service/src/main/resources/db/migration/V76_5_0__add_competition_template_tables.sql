CREATE TABLE `form_input_template` (
  `id` BIGINT(20) NOT NULL,
  `form_input_type_id` BIGINT(20) NULL,
  `guidance_question` varchar(5000) DEFAULT NULL,
  `guidance_answer` varchar(5000) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `included_in_application_summary` tinyint(1) NOT NULL DEFAULT '1',
  `priority` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fit_fitype` (`form_input_type_id`),
  CONSTRAINT `FK_fit_fitype` FOREIGN KEY (`form_input_type_id`) REFERENCES `form_input_type` (`id`));
  
CREATE TABLE `form_input_template_form_validator` (
  `form_input_template_id` BIGINT(20) NOT NULL,
  `form_validator_id` BIGINT(20) NOT NULL,
   PRIMARY KEY (`form_input_template_id`,`form_validator_id`),
   KEY `FK_fitv_fitid` (`form_input_template_id`),
   KEY `FK_fitv_fvid` (`form_validator_id`),
   CONSTRAINT `FK_fitv_fitid` FOREIGN KEY (`form_input_template_id`) REFERENCES `form_input_template` (`id`),
   CONSTRAINT `FK_fitv_fvid` FOREIGN KEY (`form_validator_id`) REFERENCES `form_validator` (`id`)
);

CREATE TABLE `competition_template` (
  `id` BIGINT(20) NOT NULL,
  `competition_type_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ct_ctid` (`competition_type_id`),
  CONSTRAINT `FK_ct_ctid` FOREIGN KEY (`competition_type_id`) REFERENCES `competition_type` (`id`)
);

CREATE TABLE `section_template` (
  `id` BIGINT(20) NOT NULL,
  `section_type` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(5000) DEFAULT NULL,
  `assessor_guidance_description` varchar(5000) DEFAULT NULL,
  `parent_section_template_id` BIGINT(20) DEFAULT NULL,
  `competition_template_id` BIGINT(20) NOT NULL,
  `priority` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_st_pstid` (`parent_section_template_id`),
  KEY `FK_st_ctid` (`competition_template_id`),
  CONSTRAINT `FK_st_pstid` FOREIGN KEY (`parent_section_template_id`) REFERENCES `section_template` (`id`),
  CONSTRAINT `FK_st_ctid` FOREIGN KEY (`competition_template_id`) REFERENCES `competition_template` (`id`)
);

CREATE TABLE `question_template` (
  `id` BIGINT(20) NOT NULL,
  `section_template_id` BIGINT(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `question_number` varchar(255) DEFAULT NULL,
  `description` varchar(5000) DEFAULT NULL,
  `assessor_guidance_question` varchar(5000) DEFAULT NULL,
  `assessor_guidance_answer` varchar(5000) DEFAULT NULL,
  `priority` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_qqt_stid` (`section_template_id`),
  CONSTRAINT `FK_qqt_stid` FOREIGN KEY (`section_template_id`) REFERENCES `section_template` (`id`)
  );
  
CREATE TABLE `question_template_form_input_template` (
  `question_template_id` BIGINT(20) NOT NULL,
  `form_input_template_id` BIGINT(20) NOT NULL,
   PRIMARY KEY (`question_template_id`,`form_input_template_id`),
   KEY `FK_qtfit_fitid` (`form_input_template_id`),
   KEY `FK_qtfit_qtid` (`question_template_id`),
   CONSTRAINT `FK_qtfit_fitid` FOREIGN KEY (`form_input_template_id`) REFERENCES `form_input_template` (`id`),
   CONSTRAINT `FK_qtfit_qtid` FOREIGN KEY (`question_template_id`) REFERENCES `question_template` (`id`)
);



