CREATE TABLE horizon_work_programme (
id bigint(20) NOT NULL AUTO_INCREMENT,
name varchar(255) COLLATE utf8_bin NOT NULL,
parent_id bigint(20) DEFAULT NULL,
enabled bit NOT NULL,
PRIMARY KEY (`id`),
CONSTRAINT `work_programme_fk` FOREIGN KEY (`parent_id`) REFERENCES `horizon_work_programme` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


INSERT INTO horizon_work_programme VALUES (1, 'Culture, Creativity and Inclusive Society (CL2)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (2, 'Civil Security for Society (CL3)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (3, 'Digital, Industry and Space (CL4 & EUSPA)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (4, 'Climate, Energy and Mobility (CL5)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (5, 'Food, Bioeconomy, Natural Resources, Agriculture and Environment (CL6)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (6, 'EIC (EIC)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (7, 'European Innovation Ecosystems (EIE)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (8, 'Health (HLTH)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (9, 'Research Infrastructures (INFRA)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (10, 'Missions (MISS)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (11, 'Widening Participation and Strengthening the European Research Area (WIDERA)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (12, 'Euratom Research and Training Programme (EURATOM)', NULL, 1);
INSERT INTO horizon_work_programme VALUES (13, 'Joint Undertaking', NULL, 1);
INSERT INTO horizon_work_programme VALUES (14, 'European Metrology Partnership', NULL, 1);

INSERT INTO horizon_work_programme VALUES (15,'HORIZON-CL2-2021-DEMOCRACY-01', 1, 1);
INSERT INTO horizon_work_programme VALUES (16,'HORIZON-CL2-2021-HERITAGE-01', 1, 1);
INSERT INTO horizon_work_programme VALUES (17,'HORIZON-CL2-2021-HERITAGE-02', 1, 1);
INSERT INTO horizon_work_programme VALUES (18,'HORIZON-CL2-2021-TRANSFORMATIONS-01', 1, 1);
INSERT INTO horizon_work_programme VALUES (19, 'HORIZON-CL2-2022-DEMOCRACY-01', 1, 1);
INSERT INTO horizon_work_programme VALUES (20, 'HORIZON-CL2-2022-HERITAGE-01', 1, 1);
INSERT INTO horizon_work_programme VALUES (21, 'HORIZON-CL2-2022-TRANSFORMATIONS-01', 1, 1);
INSERT INTO horizon_work_programme VALUES (22, 'Grants not subject to calls for proposals', 1, 1);

INSERT INTO horizon_work_programme VALUES (23,'HORIZON-CL3-2021-BM-01', 2, 1);
INSERT INTO horizon_work_programme VALUES (24,'HORIZON-CL3-2021-CS-01', 2, 1);
INSERT INTO horizon_work_programme VALUES (25,'HORIZON-CL3-2021-DRS-01', 2, 1);
INSERT INTO horizon_work_programme VALUES (26,'HORIZON-CL3-2021-FCT-01', 2, 1);
INSERT INTO horizon_work_programme VALUES (27,'HORIZON-CL3-2021-INFRA-01', 2, 1);
INSERT INTO horizon_work_programme VALUES (28,'HORIZON-CL3-2021-SSRI-01', 2, 1);
INSERT INTO horizon_work_programme VALUES (29, 'Grants not subject to calls for proposals', 2, 1);

INSERT INTO horizon_work_programme VALUES (30,'HORIZON-CL4-2021-DATA-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (31,'HORIZON-CL4-2021-DIGITAL-EMERGING-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (32,'HORIZON-CL4-2021-DIGITAL-EMERGING-02', 3, 1);
INSERT INTO horizon_work_programme VALUES (33,'HORIZON-CL4-2021-HUMAN-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (34,'HORIZON-CL4-2021-RESILIENCE-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (35,'HORIZON-CL4-2021-RESILIENCE-02', 3, 1);
INSERT INTO horizon_work_programme VALUES (36,'HORIZON-CL4-2021-SPACE-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (37,'HORIZON-CL4-2021-TWIN-TRANSITION-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (38,'HORIZON-EUSPA-2021-SPACE-02', 3, 1);
INSERT INTO horizon_work_programme VALUES (39, 'HORIZON-CL4-2022-DATA-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (40, 'HORIZON-CL4-2022-DIGITAL-EMERGING-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (41, 'HORIZON-CL4-2022-HUMAN-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (42, 'HORIZON-CL4-2022-RESILIENCE-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (43, 'HORIZON-CL4-2022-RESILIENCE-02-PCP', 3, 1);
INSERT INTO horizon_work_programme VALUES (44, 'HORIZON-CL4-2022-SPACE-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (45, 'HORIZON-CL4-2022-TWIN-TRANSITION-01', 3, 1);
INSERT INTO horizon_work_programme VALUES (46, 'Grants not subject to calls for proposals', 3, 1);

INSERT INTO horizon_work_programme VALUES (47,'HORIZON-CL5-2021-D1-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (48,'HORIZON-CL5-2021-D2-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (49,'HORIZON-CL5-2021-D3-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (50,'HORIZON-CL5-2021-D3-02', 4, 1);
INSERT INTO horizon_work_programme VALUES (51,'HORIZON-CL5-2021-D3-03', 4, 1);
INSERT INTO horizon_work_programme VALUES (52,'HORIZON-CL5-2021-D4-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (53,'HORIZON-CL5-2021-D4-02', 4, 1);
INSERT INTO horizon_work_programme VALUES (54,'HORIZON-CL5-2021-D5-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (55,'HORIZON-CL5-2021-D6-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (56, 'HORIZON-CL5-2022-D1-02', 4, 1);
INSERT INTO horizon_work_programme VALUES (57, 'HORIZON-CL5-2022-D3-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (58, 'HORIZON-CL5-2022-D5-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (59, 'HORIZON-CL5-2022-D6-01', 4, 1);
INSERT INTO horizon_work_programme VALUES (60, 'Grants not subject to calls for proposals', 4, 1);

INSERT INTO horizon_work_programme VALUES (61,'HORIZON-CL6-2021-BIODIV-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (62,'HORIZON-CL6-2021-BIODIV-02', 5, 1);
INSERT INTO horizon_work_programme VALUES (63,'HORIZON-CL6-2021-CIRCBIO-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (64,'HORIZON-CL6-2021-CLIMATE-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (65,'HORIZON-CL6-2021-COMMUNITIES-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (66,'HORIZON-CL6-2021-FARM2FORK-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (67,'HORIZON-CL6-2021-GOVERNANCE-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (68,'HORIZON-CL6-2021-ZEROPOLLUTION-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (69, 'HORIZON-CL6-2022-BIODIV-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (70, 'HORIZON-CL6-2022-CIRCBIO-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (71, 'HORIZON-CL6-2022-CLIMATE-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (72, 'HORIZON-CL6-2022-COMMUNITIES-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (73, 'HORIZON-CL6-2022-FARM2FORK-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (74, 'HORIZON-CL6-2022-GOVERNANCE-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (75, 'HORIZON-CL6-2022-ZEROPOLLUTION-01', 5, 1);
INSERT INTO horizon_work_programme VALUES (76, 'Grants not subject to calls for proposals', 5, 1);

INSERT INTO horizon_work_programme VALUES (77,'HORIZON-EIC-2021-EEN-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (78,'HORIZON-EIC-2021-ACCELERATORCHALLENGES-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (79,'HORIZON-EIC-2021-ACCELERATOROPEN-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (80, 'HORIZON-EIC-2021-NCP-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (81, 'HORIZON-EIC-2021-PATHFINDERCHALLENGES-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (82, 'HORIZON-EIC-2021-PATHFINDEROPEN-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (83, 'HORIZON-EIC-2021-TRANSITIONCHALLENGES-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (84, 'HORIZON-EIC-2021-TRANSITIONOPEN-01', 6, 1);
INSERT INTO horizon_work_programme VALUES (85, 'Grants not subject to calls for proposals', 6, 1);

INSERT INTO horizon_work_programme VALUES (86,'HORIZON-EIE-2021-CONNECT-01', 7, 1);
INSERT INTO horizon_work_programme VALUES (87,'HORIZON-EIE-2021-INNOVSMES-01', 7, 1);
INSERT INTO horizon_work_programme VALUES (88,'HORIZON-EIE-2021-SCALEUP-01', 7, 1);
INSERT INTO horizon_work_programme VALUES (89, 'HORIZON-EIE-2022-CONNECT-01', 7, 1);
INSERT INTO horizon_work_programme VALUES (90, 'Grants not subject to calls for proposals', 7, 1);

INSERT INTO horizon_work_programme VALUES (91,'HORIZON-HLTH-2021-CARE-05', 8, 1);
INSERT INTO horizon_work_programme VALUES (92,'HORIZON-HLTH-2021-DISEASE-04', 8, 1);
INSERT INTO horizon_work_programme VALUES (93,'HORIZON-HLTH-2021-ENVHLTH-02', 8, 1);
INSERT INTO horizon_work_programme VALUES (94,'HORIZON-HLTH-2021-ENVHLTH-03', 8, 1);
INSERT INTO horizon_work_programme VALUES (95,'HORIZON-HLTH-2021-IND-07', 8, 1);
INSERT INTO horizon_work_programme VALUES (96,'HORIZON-HLTH-2021-STAYHLTH-01', 8, 1);
INSERT INTO horizon_work_programme VALUES (97,'HORIZON-HLTH-2021-TOOL-06', 8, 1);
INSERT INTO horizon_work_programme VALUES (98, 'HORIZON-HLTH-2022-CARE-08', 8, 1);
INSERT INTO horizon_work_programme VALUES (99, 'HORIZON-HLTH-2022-CARE-10', 8, 1);
INSERT INTO horizon_work_programme VALUES (100, 'HORIZON-HLTH-2022-DISEASE-03', 8, 1);
INSERT INTO horizon_work_programme VALUES (101, 'HORIZON-HLTH-2022-DISEASE-07', 8, 1);
INSERT INTO horizon_work_programme VALUES (102, 'HORIZON-HLTH-2022-ENVHLTH-04', 8, 1);
INSERT INTO horizon_work_programme VALUES (103, 'HORIZON-HLTH-2022-IND-13', 8, 1);
INSERT INTO horizon_work_programme VALUES (104, 'HORIZON-HLTH-2022-STAYHLTH-02', 8, 1);
INSERT INTO horizon_work_programme VALUES (105, 'HORIZON-HLTH-2022-TOOL-11', 8, 1);
INSERT INTO horizon_work_programme VALUES (106, 'Grants not subject to calls for proposals', 8, 1);

INSERT INTO horizon_work_programme VALUES (107,'HORIZON-INFRA-2021-DEV-01', 9, 1);
INSERT INTO horizon_work_programme VALUES (108,'HORIZON-INFRA-2021-DEV-02', 9, 1);
INSERT INTO horizon_work_programme VALUES (109,'HORIZON-INFRA-2021-EOSC-01', 9, 1);
INSERT INTO horizon_work_programme VALUES (110,'HORIZON-INFRA-2021-NET-01-FPA', 9, 1);
INSERT INTO horizon_work_programme VALUES (111,'HORIZON-INFRA-2021-SERV-01', 9, 1);
INSERT INTO horizon_work_programme VALUES (112,'HORIZON-INFRA-2021-TECH-01', 9, 1);
INSERT INTO horizon_work_programme VALUES (113, 'HORIZON-INFRA-2022-DEV-01', 9, 1);
INSERT INTO horizon_work_programme VALUES (114, 'HORIZON-INFRA-2022-EOSC-01', 9, 1);
INSERT INTO horizon_work_programme VALUES (115, 'HORIZON-INFRA-2022-TECH-01', 9, 1);
INSERT INTO horizon_work_programme VALUES (116, 'Grants not subject to calls for proposals', 9, 1);

INSERT INTO horizon_work_programme VALUES (117,'HORIZON-MISS-2021-CIT-01', 10, 1);
INSERT INTO horizon_work_programme VALUES (118,'HORIZON-MISS-2021-CLIMA-01', 10, 1);
INSERT INTO horizon_work_programme VALUES (119,'HORIZON-MISS-2021-COOR-01', 10, 1);
INSERT INTO horizon_work_programme VALUES (120,'HORIZON-MISS-2021-NEB-01', 10, 1);
INSERT INTO horizon_work_programme VALUES (121,'HORIZON-MISS-2021-OCEAN-01', 10, 1);
INSERT INTO horizon_work_programme VALUES (122,'HORIZON-MISS-2021-SOIL-01', 10, 1);
INSERT INTO horizon_work_programme VALUES (123,'HORIZON-MISS-2021-SOIL-02', 10, 1);
INSERT INTO horizon_work_programme VALUES (124,'HORIZON-MISS-2021-UNCAN-01', 10, 1);
INSERT INTO horizon_work_programme VALUES (125, 'Grants not subject to calls for proposals', 10, 1);

INSERT INTO horizon_work_programme VALUES (126,'HORIZON-WIDERA-2021-ACCESS-02', 11, 1);
INSERT INTO horizon_work_programme VALUES (127,'HORIZON-WIDERA-2021-ACCESS-03', 11, 1);
INSERT INTO horizon_work_programme VALUES (128,'HORIZON-WIDERA-2021-ACCESS-05', 11, 1);
INSERT INTO horizon_work_programme VALUES (129,'HORIZON-WIDERA-2021-ACCESS-06', 11, 1);
INSERT INTO horizon_work_programme VALUES (130,'HORIZON-WIDERA-2021-ERA-01', 11, 1);
INSERT INTO horizon_work_programme VALUES (131, 'HORIZON-WIDERA-2022-ACCESS-04', 11, 1);
INSERT INTO horizon_work_programme VALUES (132, 'HORIZON-WIDERA-2022-ACCESS-07', 11, 1);
INSERT INTO horizon_work_programme VALUES (133, 'HORIZON-WIDERA-2022-ERA-01', 11, 1);
INSERT INTO horizon_work_programme VALUES (134, 'HORIZON-WIDERA-2022-TALENTS-01', 11, 1);
INSERT INTO horizon_work_programme VALUES (135, 'HORIZON-WIDERA-2022-TALENTS-02', 11, 1);
INSERT INTO horizon_work_programme VALUES (136, 'HORIZON-WIDERA-2022-TALENTS-04', 11, 1);
INSERT INTO horizon_work_programme VALUES (137, 'Grants not subject to calls for proposals', 11, 1);

INSERT INTO horizon_work_programme VALUES (138, 'HORIZON-EURATOM-2021-NRT-01-01', 12, 1);
INSERT INTO horizon_work_programme VALUES (139, 'Grants not subject to calls for proposals', 12, 1);

INSERT INTO horizon_work_programme VALUES (140, 'HORIZON-JU-SNS-2022-STREAM-A-01', 13, 1);
INSERT INTO horizon_work_programme VALUES (141, 'HORIZON-JU-SNS-2022-STREAM-B-01', 13, 1);
INSERT INTO horizon_work_programme VALUES (142, 'HORIZON-JU-SNS-2022-STREAM-C-01', 13, 1);
INSERT INTO horizon_work_programme VALUES (143, 'HORIZON-JU-SNS-2022-STREAM-CSA-01', 13, 1);
INSERT INTO horizon_work_programme VALUES (144, 'HORIZON-JU-SNS-2022-STREAM-CSA-02', 13, 1);
INSERT INTO horizon_work_programme VALUES (145, 'HORIZON-JU-SNS-2022-STREAM-D-01', 13, 1);
INSERT INTO horizon_work_programme VALUES (146, 'HORIZON-KDT-JU-1-IA-Focus-Topic-1', 13, 1);
INSERT INTO horizon_work_programme VALUES (147, 'HORIZON-KDT-JU-2021-1-IA', 13, 1);
INSERT INTO horizon_work_programme VALUES (148, 'HORIZON-KDT-JU-2021-2-RIA-Focus-Topic-1', 13, 1);
INSERT INTO horizon_work_programme VALUES (149, 'HORIZON-KDT-JU-2021-2-RIA', 13, 1);
INSERT INTO horizon_work_programme VALUES (150, 'HORIZON-KDT-JU-2021-3-CSA', 13, 1);

INSERT INTO horizon_work_programme VALUES (151, 'HORIZON-European Metrology Partnership 2021 Call', 14, 1);