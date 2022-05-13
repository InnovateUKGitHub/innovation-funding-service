-- IFS-11939-pre registration domain model changes at competition level

ALTER TABLE competition ADD COLUMN pre_registration BIT(1) NOT NULL DEFAULT FALSE;