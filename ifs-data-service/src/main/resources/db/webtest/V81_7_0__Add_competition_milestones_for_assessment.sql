SELECT @jugglingCraziness := id FROM competition WHERE name='Juggling Craziness';
SELECT @sarcasmStupendousness := id FROM competition WHERE name='Sarcasm Stupendousness';
SELECT @killerRiffs := id FROM competition WHERE name='Killer Riffs';
SELECT @laFromage := id FROM competition WHERE name='La Fromage';

-- TODO this should copy the ASSESSOR_ACCEPTS and ASSESSOR_DEADLINE dates

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

