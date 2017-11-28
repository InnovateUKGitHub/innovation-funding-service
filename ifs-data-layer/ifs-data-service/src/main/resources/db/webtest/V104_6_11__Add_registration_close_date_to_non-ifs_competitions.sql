-- This is a temporary patch that will be integrated in the baseline by the generator as part of IFS-2240.
SELECT @firstcompid := id from competition WHERE name = 'Transforming big data';
SELECT @secondcompid := id from competition WHERE name = 'Reducing carbon footprints';

INSERT INTO `milestone` (`date`, `type`, `competition_id`) VALUES ('2020-06-17 11:00:00', 'REGISTRATION_DATE', @firstcompid);
INSERT INTO `milestone` (`date`, `type`, `competition_id`) VALUES ('2020-06-17 11:00:00', 'REGISTRATION_DATE', @secondcompid);
