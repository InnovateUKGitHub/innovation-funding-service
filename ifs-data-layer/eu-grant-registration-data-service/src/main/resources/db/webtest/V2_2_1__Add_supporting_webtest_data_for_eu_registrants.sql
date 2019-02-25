-- IFS-5209: Add backing data for pre-existing eu contacts in order to reference in invite emails

INSERT INTO `eu_organisation` (`name`, `companies_house_number`, `organisation_type`)
VALUES
('Slick Corp', 'abc123', 'BUSINESS'),
('Accent Systems', 'def456', 'BUSINESS'),
('Jupiter Records', 'ghi789', 'BUSINESS'),
('Drema Media', 'jkl123', 'BUSINESS'),
('Omega Acoustics', 'mno456', 'BUSINESS'),
('Caesar Arts', 'pqr789', 'BUSINESS'),
('Lemon Mart', 'stu123', 'BUSINESS'),
('Wave Media', 'vwx456', 'BUSINESS'),
('Riddle Walk', 'abc789', 'BUSINESS'),
('Shadow Navigations', 'def123', 'BUSINESS'),
('Alpine Corp', 'ghi456', 'BUSINESS');
--
INSERT INTO `eu_funding` (`grant_agreement_number`, `participant_id`, `eu_action_type_id`, `project_name`, `project_start_date`, `project_end_date`, `funding_contribution`, `project_coordinator`)
VALUES
("123", "1", 1 ,"project", now(), now(), 20, 0),
("456", "1", 1 ,"project", now(), now(), 20, 0),
("789", "1", 1 ,"project", now(), now(), 20, 0),
("abc", "1", 1 ,"project", now(), now(), 20, 0),
("def", "1", 1 ,"project", now(), now(), 20, 0),
("ghi", "1", 1 ,"project", now(), now(), 20, 0),
("jkl", "1", 1 ,"project", now(), now(), 20, 0),
("mno", "1", 1 ,"project", now(), now(), 20, 0),
("pqr", "1", 1 ,"project", now(), now(), 20, 0),
("stv", "1", 1 ,"project", now(), now(), 20, 0);
--
--
INSERT INTO `eu_grant` (`id`, `eu_organisation_id`, `eu_contact_id`, `eu_funding_id`, `submitted`, `short_code`, `created_on`, `modified_on`)
VALUES
(1,1,1,1,1, "ABC123", now(), now()),
(2,2,2,2,1, "DEF456", now(), now()),
(3,3,3,3,1, "GHI789", now(), now()),
(4,4,4,4,1, "JKL123", now(), now()),
(5,5,5,5,1, "MNO456", now(), now()),
(6,6,6,6,1, "PQR789", now(), now()),
(7,7,7,7,1, "STU123", now(), now()),
(8,8,8,8,1, "VWX456", now(), now()),
(9,9,9,9,1, "YZA789", now(), now()),
(10,10,10,10,1, "BCD123", now(), now());