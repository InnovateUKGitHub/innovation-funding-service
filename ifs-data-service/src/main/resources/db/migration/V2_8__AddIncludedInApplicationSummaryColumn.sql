--
-- Add column to determine which form inputs to show in the application summary, and exclude the file appendices
--
alter table form_input add column included_in_application_summary tinyint(1) NOT NULL DEFAULT true;
update form_input set included_in_application_summary = false where form_input_type_id = 4;