SELECT @jugglingCraziness := id FROM competition WHERE name='Sustainable living models for the future';
SELECT @sarcasmStupendousness := id FROM competition WHERE name='Photonics for health';
SELECT @killerRiffs := id FROM competition WHERE name='New designs for a circular economy';
SELECT @laFromage := id FROM competition WHERE name='Internet of Things';
SELECT @developmentOfNewTech := id FROM competition WHERE name like 'Development of new technological%';

-- Add ASSESSORS_NOTIFIED and ASSESSMENT_CLOSED milestones for project setup tests
INSERT INTO milestone (date, type, competition_id) VALUES ('2018-01-12 00:00:00', 'ASSESSORS_NOTIFIED', @sarcasmStupendousness);
INSERT INTO milestone (date, type, competition_id) VALUES ('2018-12-31 00:00:00', 'ASSESSMENT_CLOSED', @sarcasmStupendousness);

-- Add ASSESSORS_NOTIFIED and ASSESSMENT_CLOSED milestones for comp admin tests
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-01-12 00:00:00', 'ASSESSORS_NOTIFIED', @jugglingCraziness);
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-12-31 00:00:00', 'ASSESSMENT_CLOSED', @jugglingCraziness);

-- killer riffs for tests.Project Setup.Experian Feedback.Project Finance can see Bank Details
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-01-12 00:00:00', 'ASSESSORS_NOTIFIED', @killerRiffs);
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-01-29 00:00:00', 'ASSESSMENT_CLOSED', @killerRiffs);

-- lf fromage for 05 project setup.Finance checks
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-04-12 00:00:00', 'ASSESSORS_NOTIFIED', @laFromage);
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-05-12 00:00:00', 'ASSESSMENT_CLOSED', @laFromage);

INSERT INTO milestone (date, type, competition_id) VALUES ('2016-01-12 00:00:00', 'ASSESSORS_NOTIFIED', @developmentOfNewTech);
INSERT INTO milestone (date, type, competition_id) VALUES ('2016-01-29 00:00:00', 'ASSESSMENT_CLOSED', @developmentOfNewTech);
