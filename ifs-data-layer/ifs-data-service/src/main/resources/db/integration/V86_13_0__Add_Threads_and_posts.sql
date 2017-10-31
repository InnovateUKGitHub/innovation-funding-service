INSERT IGNORE INTO thread (id, class_pk, class_name, thread_type, title, created_on, section) VALUES (1, 6, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'QUERY', 'Query1', '2017-02-08 09:00:00', 'ELIGIBILITY');
INSERT IGNORE INTO post (id, thread_id, author_id, body, created_on) VALUES (1, 1, 18, 'What do we want?', '2017-02-08 09:01:00');

INSERT IGNORE INTO thread (id, class_pk, class_name, thread_type, title, created_on, section) VALUES (2,6, 'org.innovateuk.ifs.finance.domain.ProjectFinance', 'QUERY', 'Query2', '2017-02-08 09:05:00', 'ELIGIBILITY');
INSERT IGNORE INTO post (id, thread_id, author_id, body, created_on) VALUES (3, 2, 18, 'Why do we want?', '2017-02-08 09:06:00');