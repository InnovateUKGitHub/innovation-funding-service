SELECT MAX(id) INTO @org_id FROM organisation;
SELECT MAX(id) INTO @user_id FROM user;

-- set up users
SELECT id INTO @pf_role_id FROM role WHERE name='project_finance';
SELECT id INTO @pm_role_id FROM role WHERE name='project_manager';
SELECT MAX(id) INTO @profile_id FROM profile;
INSERT INTO profile (id, created_by, created_on, modified_by, modified_on) values (@profile_id + 1, @user_id, NOW(), @user_id, NOW());
INSERT INTO user (email, profile_id, uid, created_by, created_on, modified_by, modified_on) values ('pfifs2100@gmail.vom', @profile_id + 1, 'uid21001234567', @user_id, NOW(), @user_id, NOW());
SELECT id INTO @pf_user_id FROM user WHERE email='pfifs2100@gmail.vom';
INSERT INTO user_role (user_id, role_id) values (@pf_user_id, @pf_role_id);
INSERT INTO user (email, profile_id, uid, created_by, created_on, modified_by, modified_on) values ('pmifs2100@gmail.vom', @profile_id + 1, 'uid0012654321', @user_id, NOW(), @user_id, NOW());
SELECT id INTO @pm_user_id FROM user WHERE email='pmifs2100@gmail.vom';
INSERT INTO user_role (user_id, role_id) values (@pm_user_id, @pm_role_id);

INSERT INTO organisation (id, organisation_type_id, name) values (@org_id + 1, 1, 'Org1');
INSERT INTO organisation (id, organisation_type_id, name) values (@org_id + 2, 1, 'Org2');

-- 1 query created by project finance
INSERT INTO competition (competition_type_id, lead_technologist_user_id, executive_user_id, name) values(1, 51, 49, 'Comp21001');
SELECT id INTO @comp_id FROM competition WHERE name='Comp21001';
INSERT INTO application (competition, name) values (@comp_id, 'App21001');
SELECT id INTO @app_id FROM application WHERE name = 'App21001';
INSERT INTO project (name, application_id) values ('project 1', @app_id);
SELECT id INTO @proj_id FROM project WHERE name = 'project 1';
INSERT INTO partner_organisation (organisation_id, project_id, lead_organisation) values(@org_id, @proj_id, 1);
SELECT id INTO @porg_id FROM partner_organisation WHERE organisation_id = @org_id AND project_id = @proj_id;
INSERT INTO project_finance (project_id, organisation_id) values (@proj_id, @org_id + 1);
SELECT id INTO @pfin_id FROM project_finance WHERE project_id = @proj_id AND organisation_id = @org_id + 1;
INSERT INTO thread (class_pk, class_name, title, thread_type) values (@pfin_id, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread1', 'QUERY');
SELECT id INTO @thread_id FROM thread WHERE title = 'Thread1';
INSERT INTO post (thread_id, author_id, body, created_on) values (@thread_id, @pf_user_id, 'post 11', ADDDATE(NOW(), INTERVAL 1 MINUTE));
SELECT id INTO @post_id FROM post WHERE body = 'post 11';

-- 1 query created by project manager (not possible through IFS)
INSERT INTO competition (id, competition_type_id, lead_technologist_user_id, executive_user_id, name) values(@comp_id + 1, 1, 51, 49, 'Comp21002');
INSERT INTO application (id, competition, name) values (@app_id + 1, @comp_id + 1, 'App21002');
INSERT INTO project (id, name, application_id) values (@proj_id + 1, 'project 2', @app_id + 1);
INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 1, @org_id + 2, @proj_id + 1, 1);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 1, @proj_id + 1, @org_id + 2);

INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 1, @pfin_id + 1, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread2', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 1, @thread_id + 1, @pm_user_id, 'post 21', ADDDATE(NOW(), INTERVAL 2 MINUTE));

-- 1 query created by project finance with a response from the project manager
INSERT INTO competition (id, competition_type_id, lead_technologist_user_id, executive_user_id, name) values(@comp_id + 2, 1, 51, 49, 'Comp21003');
INSERT INTO application (id, competition, name) values (@app_id + 2, @comp_id + 2, 'App21003');
INSERT INTO project (id, name, application_id) values (@proj_id + 2, 'project 3', @app_id + 2);
INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 2, @org_id + 1, @proj_id + 2, 1);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 2, @proj_id + 2, @org_id + 1);

INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 2, @pfin_id + 2, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread3', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 2, @thread_id + 2, @pf_user_id, 'post 31', ADDDATE(NOW(), INTERVAL 3 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 3, @thread_id + 2, @pm_user_id, 'post 32', ADDDATE(NOW(), INTERVAL 4 MINUTE));

-- another thread on same project for same org should not produce duplicates
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 3, @pfin_id + 2, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread32', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 4, @thread_id + 3, @pf_user_id, 'post 321', ADDDATE(NOW(), INTERVAL 5 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 5, @thread_id + 3, @pm_user_id, 'post 322', ADDDATE(NOW(), INTERVAL 6 MINUTE));

-- thread on a different competition should not appear in results
INSERT INTO competition (id, competition_type_id, lead_technologist_user_id, executive_user_id, name) values(@comp_id + 3, 1, 51, 49, 'Comp21004');
INSERT INTO application (id, competition, name) values (@app_id + 3, @comp_id + 3, 'App21004');
INSERT INTO project (id, name, application_id) values (@proj_id + 3, 'project 4', @app_id + 3);
INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 3, @org_id + 1, @proj_id + 3, 1);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 3, @proj_id + 3, @org_id + 1);

INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 4, @pfin_id + 3, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread4', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 6, @thread_id + 4, @pf_user_id, 'post 41', ADDDATE(NOW(), INTERVAL 7 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 7, @thread_id + 4, @pm_user_id, 'post 42', ADDDATE(NOW(), INTERVAL 8 MINUTE));

-- notes are not considered queries
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 5, @pfin_id + 3, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread5', 'NOTE');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 8, @thread_id + 5, @pf_user_id, 'note 1', ADDDATE(NOW(), INTERVAL 9 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 9, @thread_id + 5, @pm_user_id, 'note 2', ADDDATE(NOW(), INTERVAL 10 MINUTE));

-- queries not keyed on ProjectFinance are ignored
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 6, @pfin_id + 3, 'org.innovateuk.ifs.finance.domain.Wibble', 'Thread6', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 10, @thread_id + 6, @pf_user_id, 'Wobble 1', ADDDATE(NOW(), INTERVAL 11 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 11, @thread_id + 6, @pm_user_id, 'Wobble 2', ADDDATE(NOW(), INTERVAL 12 MINUTE));

-- 2 partners have open queries
INSERT INTO competition (id, competition_type_id, lead_technologist_user_id, executive_user_id, name) values(@comp_id + 4, 1, 51, 49, 'Comp21005');
INSERT INTO application (id, competition, name) values (@app_id + 4, @comp_id + 4, 'App21005');
INSERT INTO project (id, name, application_id) values (@proj_id + 4, 'project 5', @app_id + 4);
INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 4, @org_id + 1, @proj_id + 4, 1);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 4, @proj_id + 4, @org_id + 1);
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 7, @pfin_id + 4, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread7', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 12, @thread_id + 7, @pf_user_id, 'post 71', ADDDATE(NOW(), INTERVAL 13 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 13, @thread_id + 7, @pm_user_id, 'post 72', ADDDATE(NOW(), INTERVAL 14 MINUTE));

INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 5, @org_id + 2, @proj_id + 4, 0);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 5, @proj_id + 4, @org_id + 2);
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 8, @pfin_id + 5, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread8', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 14, @thread_id + 8, @pf_user_id, 'post 81', ADDDATE(NOW(), INTERVAL 15 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 15, @thread_id + 8, @pm_user_id, 'post 82', ADDDATE(NOW(), INTERVAL 16 MINUTE));

-- 2 projects in the same competition have open queries, check ordering app id
INSERT INTO competition (id, competition_type_id, lead_technologist_user_id, executive_user_id, name) values(@comp_id + 5, 1, 51, 49, 'Comp21006');
INSERT INTO application (id, competition, name) values (@app_id + 5, @comp_id + 5, 'App21006a');
INSERT INTO project (id, name, application_id) values (@proj_id + 5, 'project 6', @app_id + 5);
INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 6, @org_id + 1, @proj_id + 5, 1);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 6, @proj_id + 5, @org_id + 1);
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 9, @pfin_id + 6, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread9', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 16, @thread_id + 9, @pf_user_id, 'post 91', ADDDATE(NOW(), INTERVAL 17 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 17, @thread_id + 9, @pm_user_id, 'post 92', ADDDATE(NOW(), INTERVAL 18 MINUTE));

INSERT INTO application (id, competition, name) values (@app_id + 6, @comp_id + 5, 'App21006b');
INSERT INTO project (id, name, application_id) values (@proj_id + 6, 'project 7', @app_id + 6);
INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 7, @org_id + 1, @proj_id + 6, 0);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 7, @proj_id + 6, @org_id + 1);
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 10, @pfin_id + 7, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread10', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 18, @thread_id + 10, @pf_user_id, 'post 101', ADDDATE(NOW(), INTERVAL 19 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 19, @thread_id + 10, @pm_user_id, 'post 102', ADDDATE(NOW(), INTERVAL 20 MINUTE));

-- open query with spend profile generated is ignored
INSERT INTO competition (id, competition_type_id, lead_technologist_user_id, executive_user_id, name) values(@comp_id + 6, 1, 51, 49, 'Comp21007');
INSERT INTO application (id, competition, name) values (@app_id + 7, @comp_id + 6, 'App21007');
INSERT INTO project (id, name, application_id) values (@proj_id + 7, 'project 8', @app_id + 7);
INSERT INTO partner_organisation (id, organisation_id, project_id, lead_organisation) values(@porg_id + 8, @org_id + 1, @proj_id + 7, 1);
INSERT INTO project_finance (id, project_id, organisation_id) values (@pfin_id + 8, @proj_id + 7, @org_id + 1);
INSERT INTO thread (id, class_pk, class_name, title, thread_type) values (@thread_id + 11, @pfin_id + 8, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'Thread11', 'QUERY');
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 20, @thread_id + 11, @pf_user_id, 'post 111', ADDDATE(NOW(), INTERVAL 21 MINUTE));
INSERT INTO post (id, thread_id, author_id, body, created_on) values (@post_id + 21, @thread_id + 11, @pm_user_id, 'post 112', ADDDATE(NOW(), INTERVAL 22 MINUTE));

INSERT INTO cost_category_group (description) values ('ccg_1');
SELECT id INTO @ccg_id FROM cost_category_group WHERE description='ccg_1';
INSERT INTO cost_category_type (name, cost_category_group_id) values ('cgt_1', @ccg_id);
SELECT id INTO @cgt_id FROM cost_category_type WHERE name='cgt_1';
INSERT INTO cost_group (description) values ('cg_1');
SELECT id INTO @cg_id FROM cost_group WHERE description='cg_1';
INSERT INTO spend_profile (organisation_id, project_id, generated_by_id, cost_category_type_id, eligible_costs_cost_group_id, spend_profile_figures_cost_group_id, generated_date)
values(@org_id + 1, @proj_id + 7, @pf_user_id, @cgt_id, @cg_id, @cg_id, NOW());
