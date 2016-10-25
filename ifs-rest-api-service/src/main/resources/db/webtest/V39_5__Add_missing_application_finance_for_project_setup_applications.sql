SELECT @leadApplicantRoleId := id FROM role WHERE `name` = 'leadapplicant';

INSERT IGNORE INTO application_finance (application_id, organisation_id, organisation_size, finance_file_entry_id)
  SELECT a.id, pu.organisation_id, 'SMALL', null FROM application a
    JOIN project proj ON proj.application_id = a.id
    JOIN project_user pu ON pu.project_id = proj.id
    WHERE NOT EXISTS (
      SELECT 1 FROM application_finance a2
      WHERE a2.application_id = a.id AND a2.organisation_id = pu.organisation_id
    );