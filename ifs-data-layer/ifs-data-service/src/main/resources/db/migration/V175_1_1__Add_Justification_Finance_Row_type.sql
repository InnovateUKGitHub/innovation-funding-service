-- IFS-6207 Update new finance row type

ALTER TABLE application_finance
  ADD COLUMN justification longtext;