-- IFS-2994: Create a new role for External finance user

INSERT INTO role
(id, name, url)
VALUES
(22, 'external_finance', 'management/dashboard');