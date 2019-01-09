-- IFS-4959 - add a new role to identify a user as having live projects and so access to an external system for
-- handling the live projects

INSERT INTO role
(id, name)
VALUES
(21, 'live_projects_user');

