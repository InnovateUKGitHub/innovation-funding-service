-- Add fields to represent financial year end, overview and staff count.
-- A concrete types is required, not just generic TEXTINPUT, as there is logic to activate them under certain conditions and so this provides a way to identify them.
-- A id is provided as this is referenced in the code and so must be the same across environments.
INSERT INTO `form_input_type` (`id`, `name`) VALUES (26, 'FINANCIAL_YEAR_END');
INSERT INTO `form_input_type` (`id`, `name`) VALUES (27, 'FINANCIAL_OVERVIEW_ROW');
INSERT INTO `form_input_type` (`id`, `name`) VALUES (28, 'FINANCIAL_STAFF_COUNT');



