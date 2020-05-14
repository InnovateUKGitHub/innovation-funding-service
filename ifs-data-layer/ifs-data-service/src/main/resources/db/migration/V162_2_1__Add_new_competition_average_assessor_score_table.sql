-- IFS-7369: Make assessor score available in notifications: Domain for config & internal comp setup journey
CREATE TABLE competition_assessment_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  temporary_competition_id bigint(20) NOT NULL,
  include_average_assessor_score_in_notifications BIT(1) DEFAULT NULL,
  has_assessment_panel BIT(1) DEFAULT NULL,
  has_interview_stage BIT(1) DEFAULT NULL,
  assessor_count int(4) DEFAULT '0',
  assessor_pay decimal(10,2) DEFAULT '0.00',
  assessor_finance_view enum('OVERVIEW','DETAILED') NOT NULL DEFAULT 'OVERVIEW',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;