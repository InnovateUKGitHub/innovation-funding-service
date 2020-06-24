CREATE TABLE multiple_choice_option (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  text varchar(255) NOT NULL,
  form_input_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT multiple_choice_option_to_form_input_fk FOREIGN KEY (form_input_id) REFERENCES form_input (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;