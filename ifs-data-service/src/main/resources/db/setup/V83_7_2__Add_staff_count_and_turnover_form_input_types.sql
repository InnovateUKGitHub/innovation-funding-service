-- Add fields to represent staff turnover and staff count.
-- A concrete types is required, not just generic TEXTINPUT, as there is logic to activate them under certain conditions and so this provides a way to identify them.
-- A id is provided as this is referenced in the code and so must be the same across environments.
INSERT INTO `form_input_type` (`id`, `name`) VALUES (24, 'STAFF_TURNOVER');
INSERT INTO `form_input_type` (`id`, `name`) VALUES (25, 'STAFF_COUNT');
