-- IFS-11943-pre registration domain model changes at section and question level

ALTER TABLE section ADD COLUMN pre_registration BIT(1) NOT NULL DEFAULT TRUE;

ALTER TABLE question ADD COLUMN pre_registration BIT(1) NOT NULL DEFAULT TRUE;