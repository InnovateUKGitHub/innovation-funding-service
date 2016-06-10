#
# Adding Steve Smith as a collaborator in the lead organisation for application 16 (Cheese is Good)
#
insert into process_role (application_id, organisation_id, role_id, user_id) values (16, 21, 2, 1);   

#
# Adding Jessica Doe and Pete Tom as collaborators in different organisations 
#
insert into process_role (application_id, organisation_id, role_id, user_id) values (16, 4, 2, 2), (16, 6, 2, 8);

