CREATE TABLE `question_assessment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) NOT NULL,
  `scored` bit(1),
  `written_feedback` bit(1),
  `score_total` int(11) DEFAULT '0',
  `word_count` int(11) DEFAULT '0',
  `guidance` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_condition_question_idx` (`question_id`),
  CONSTRAINT `fk_condition_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `assessment_score_row` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_assessment_id` bigint(20) NOT NULL,
  `start` int(11) DEFAULT '0',
  `end` int(11) DEFAULT '0',
  `justification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_condition_question_assessment_idx` (`question_assessment_id`),
  CONSTRAINT `fk_condition_question_assessment` FOREIGN KEY (`question_assessment_id`) REFERENCES `question_assessment` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
