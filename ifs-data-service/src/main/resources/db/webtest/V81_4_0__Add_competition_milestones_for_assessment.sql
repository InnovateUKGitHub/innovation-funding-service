-- Add ASSESSORS_NOTIFIED and ASSESSMENT_CLOSED milestones for project setup tests
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-01-12 00:00:00', 'ASSESSORS_NOTIFIED', 6);
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-04-14 00:00:00', 'ASSESSMENT_CLOSED', 6);

INSERT INTO milestone (date, type, competition_id) VALUES ('2016-04-12 00:00:00', 'ASSESSORS_NOTIFIED', 3);
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-04-14 00:00:00', 'ASSESSMENT_CLOSED', 3);

INSERT INTO milestone (date, type, competition_id) VALUES ('2018-01-12 00:00:00', 'ASSESSORS_NOTIFIED', 4);
INSERT INTO milestone (date, type, competition_id) VALUES ('2018-12-31 00:00:00', 'ASSESSMENT_CLOSED', 4);


-- Add ASSESSORS_NOTIFIED and ASSESSMENT_CLOSED milestones for comp admin tests
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-01-12 00:00:00', 'ASSESSORS_NOTIFIED', 2);
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-12-31 00:00:00', 'ASSESSMENT_CLOSED', 2);
