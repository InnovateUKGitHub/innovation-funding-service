-- Adding new column for requirement to allow configurable finances for academics. If flag is set then the academics
-- will see the Je-S form, otherwise they see the usual project finances.
ALTER TABLE competition ADD include_jes_form BIT(1) NULL;

-- Default all existing competitions to have include_jes_form as true field.
UPDATE competition SET include_jes_form=1 WHERE template=0;