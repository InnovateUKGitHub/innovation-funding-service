-- IFS-8846, always open competition changes

ALTER TABLE competition ADD COLUMN always_open BIT(1) DEFAULT NULL;
