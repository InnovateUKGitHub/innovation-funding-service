-- Create new Organisation with Address to be associated with a new User to eventually be invited to assess the Juggling Craziness competition
INSERT INTO address (id, address_line1, address_line2, address_line3, town, postcode, county) VALUES (30,'369','Juggle Grove','','Northampton','NN1 4EU','');
INSERT INTO organisation (id, name, company_house_number, organisation_size, organisation_type_id) VALUES (38,'British Juggling Ltd','06477798',NULL,1);
INSERT INTO organisation_address (id, address_type_id, address_id, organisation_id) VALUES (30,1,30,38);

-- Create a new user. This user will be treated as an existing User and sent an invitation to be a Competition Assessor
INSERT INTO user (id, email, image_url, first_name, invite_name, last_name, phone_number, title, status, uid, system_user) VALUES (53, 'worth.email.test+assessor1@gmail.com', 'image3.jpg', 'Assessor', null, 'One', '1234567890', 'Mr', 'ACTIVE', 'a36c4aff-7840-4cd8-b5dd-5c945b8d9959', 0);
INSERT INTO user_role (user_id, role_id) VALUES (53, 3);
INSERT INTO user_organisation (user_id, organisation_id) VALUES (53, 38);

-- Create invitations for the Juggling Craziness competition
-- Accepted
INSERT INTO invite (id, email, hash, name, status, target_id, owner_id, type) VALUES (13, 'paul.plum@gmail.com', '637b092bae07ca4cf7055d0e7457fbd88a93c27ef754d485df5dd9c9c8d27270ac7e35171f292ea7', 'Paul Plum', 'ACCEPTED', 2, 18, 'COMPETITION');
INSERT INTO invite (id, email, hash, name, status, target_id, owner_id, type) VALUES (14, 'felix.wilson@gmail.com', 'd50032c225bf7134717dc05fabfb3c580595663c8bc31404cd7a72099272017d3ce2a022ead617b4', 'Felix Wilson', 'ACCEPTED', 2, 18, 'COMPETITION');
INSERT INTO competition_user (competition_role_id, competition_id, user_id, invite_id, competition_user_status_name, rejection_reason_id, rejection_comment) VALUES (1, 2, 3, 13, 'ACCEPTED', null, null);
INSERT INTO competition_user (competition_role_id, competition_id, user_id, invite_id, competition_user_status_name, rejection_reason_id, rejection_comment) VALUES (1, 2, 9, 14, 'ACCEPTED', null, null);

-- Pending
INSERT INTO invite (id, email, hash, name, status, target_id, owner_id, type) VALUES (15, 'worth.email.test+assessor1@gmail.com', 'bcbf56004fddd137ea29d4f8434d33f62e7a7552a3a084197c7dfebce774c136c10bb26e1c6c989e', 'Assessor One', 'SEND', 2, 18, 'COMPETITION');
INSERT INTO invite (id, email, hash, name, status, target_id, owner_id, type) VALUES (16, 'worth.email.test+assessor2@gmail.com', '2abe401d357fc486da56d2d34dc48d81948521b372baff98876665f442ee50a1474a41f5a0964720', 'Assessor Two', 'SEND', 2, 18, 'COMPETITION');
INSERT INTO invite (id, email, hash, name, status, target_id, owner_id, type) VALUES (17, 'worth.email.test+assessor3@gmail.com', '1e05f43963cef21ec6bd5ccd6240100d35fb69fa16feacb9d4b77952bf42193842c8e73e6b07f932', 'Assessor Three', 'SEND', 2, 18, 'COMPETITION');
INSERT INTO competition_user (competition_role_id, competition_id, user_id, invite_id, competition_user_status_name, rejection_reason_id, rejection_comment) VALUES (1, 2, 53, 15, 'PENDING', null, null);
INSERT INTO competition_user (competition_role_id, competition_id, user_id, invite_id, competition_user_status_name, rejection_reason_id, rejection_comment) VALUES (1, 2, null, 16, 'PENDING', null, null);
INSERT INTO competition_user (competition_role_id, competition_id, user_id, invite_id, competition_user_status_name, rejection_reason_id, rejection_comment) VALUES (1, 2, null, 17, 'PENDING', null, null);
