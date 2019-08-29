--  IFS-6307 making project setup columns configurable

CREATE TABLE competition_project_setup_columns (
  competition_id bigint(20) NOT NULL,
  project_setup_column enum (
    'PROJECT_DETAILS',
    'PROJECT_TEAM',
    'DOCUMENTS',
    'MONITORING_OFFICER',
    'BANK_DETAILS',
    'FINANCE_CHECKS',
    'SPEND_PROFILE',
    'GRANT_OFFER_LETTER'
  ) NOT NULL,
  PRIMARY KEY (competition_id,project_setup_column),
  CONSTRAINT fk_competition_project_setup_columns_competition_id FOREIGN KEY (competition_id) REFERENCES competition(id)
);

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'PROJECT_DETAILS' as project_setup_column
FROM competition;

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'PROJECT_TEAM' as project_setup_column
FROM competition;

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'DOCUMENTS' as project_setup_column
FROM competition;

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'MONITORING_OFFICER' as project_setup_column
FROM competition;

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'BANK_DETAILS' as project_setup_column
FROM competition;

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'FINANCE_CHECKS' as project_setup_column
FROM competition;

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'SPEND_PROFILE' as project_setup_column
FROM competition;

INSERT INTO competition_project_setup_columns (competition_id, project_setup_column)
SELECT id as competition_id, 'GRANT_OFFER_LETTER' as project_setup_column
FROM competition;