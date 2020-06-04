-- new user https://ifs.local-dev/management/monitoring-officer/hash123/register
INSERT INTO invite (email, hash, name, status, target_id, owner_id, type, innovation_category_id, sent_by, sent_on)
VALUES ('tom@poly.io', 'hash123', 'Tom Baldwin', 'SENT', NULL, NULL, 'MONITORING_OFFICER', NULL, 20, now());
-- existing user https://ifs.local-dev/management/monitoring-officer/hash456/register
INSERT INTO invite (email, hash, name, status, target_id, owner_id, type, innovation_category_id, sent_by, sent_on)
VALUES ('steve.smith@empire.com', 'hash456', 'Steve Smith', 'SENT', NULL, NULL, 'MONITORING_OFFICER', NULL, 20, now());