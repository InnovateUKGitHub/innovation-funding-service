-- IFS-7703 - include multiple choice option to form input response
ALTER TABLE form_input_response
        ADD COLUMN multiple_choice_option_id bigint(20),
        ADD CONSTRAINT `FK_form_input_response_multiple_choice_option_id` FOREIGN KEY (`multiple_choice_option_id`) REFERENCES `multiple_choice_option` (`id`);