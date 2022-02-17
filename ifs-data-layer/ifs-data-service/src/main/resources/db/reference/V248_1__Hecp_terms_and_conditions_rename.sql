-- IFS-10696 - Horizon Europe Guarantee - T&Cs

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org'
);

UPDATE terms_and_conditions
SET
    name = 'Horizon Europe Guarantee – EIT KICs 2021',
    template = 'horizon-europe-guarantee-kics-terms-and-conditions'
WHERE name = 'Horizon Europe Contingency Programme Privacy Notice';

UPDATE terms_and_conditions
SET
    name = 'Horizon Europe Guarantee',
    template = 'horizon-europe-guarantee-terms-and-conditions'
WHERE name = 'Horizon Europe EIC Accelerator terms and conditions';