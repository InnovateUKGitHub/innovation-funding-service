-- IFS-8158 add justification to application finance

ALTER TABLE application_finance
  ADD COLUMN justification longtext;