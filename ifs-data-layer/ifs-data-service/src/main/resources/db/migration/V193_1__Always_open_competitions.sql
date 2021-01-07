-- IFS-8846, always open competition changes

ALTER TABLE competition_application_config ADD COLUMN always_open BIT(1) DEFAULT NULL;
