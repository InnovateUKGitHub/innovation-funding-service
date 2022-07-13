ALTER TABLE application_horizon_work_programme 
ADD COLUMN work_programme_id bigint(20),
ADD CONSTRAINT `FK_horizon_work_programme_id` FOREIGN KEY (`work_programme_id`) REFERENCES `horizon_work_programme` (`id`);


UPDATE application_horizon_work_programme SET work_programme_id = 1 WHERE work_programme = 'CL2';
UPDATE application_horizon_work_programme SET work_programme_id = 2 WHERE work_programme = 'CL3';
UPDATE application_horizon_work_programme SET work_programme_id = 3 WHERE work_programme = 'CL4';
UPDATE application_horizon_work_programme SET work_programme_id = 4 WHERE work_programme = 'CL5';
UPDATE application_horizon_work_programme SET work_programme_id = 5 WHERE work_programme = 'CL6';
UPDATE application_horizon_work_programme SET work_programme_id = 6 WHERE work_programme = 'EIC';
UPDATE application_horizon_work_programme SET work_programme_id = 7 WHERE work_programme = 'EIE';
UPDATE application_horizon_work_programme SET work_programme_id = 8 WHERE work_programme = 'HLTH';
UPDATE application_horizon_work_programme SET work_programme_id = 9 WHERE work_programme = 'INFRA';
UPDATE application_horizon_work_programme SET work_programme_id = 10 WHERE work_programme = 'MISS';
UPDATE application_horizon_work_programme SET work_programme_id = 11 WHERE work_programme = 'WIDERA';
UPDATE application_horizon_work_programme SET work_programme_id = 12 WHERE work_programme = 'EURATOM';
UPDATE application_horizon_work_programme SET work_programme_id = 13 WHERE work_programme = 'JOINT_UNDERTAKING';
UPDATE application_horizon_work_programme SET work_programme_id = 14 WHERE work_programme = 'EUROPEAN_METROLOGY_PARTNERSHIP';

UPDATE application_horizon_work_programme SET work_programme_id = 15 WHERE work_programme = 'HORIZON_CL2_2021_DEMOCRACY_01';
UPDATE application_horizon_work_programme SET work_programme_id = 16 WHERE work_programme = 'HORIZON_CL2_2021_HERITAGE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 17 WHERE work_programme = 'HORIZON_CL2_2021_HERITAGE_02';
UPDATE application_horizon_work_programme SET work_programme_id = 18 WHERE work_programme = 'HORIZON_CL2_2021_TRANSFORMATIONS_01';
UPDATE application_horizon_work_programme SET work_programme_id = 19 WHERE work_programme = 'HORIZON_CL2_2022_DEMOCRACY_01';
UPDATE application_horizon_work_programme SET work_programme_id = 20 WHERE work_programme = 'HORIZON_CL2_2022_HERITAGE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 21 WHERE work_programme = 'HORIZON_CL2_2022_TRANSFORMATIONS_01';
UPDATE application_horizon_work_programme SET work_programme_id = 22 WHERE work_programme = 'GRANTS_CL2';

UPDATE application_horizon_work_programme SET work_programme_id = 23 WHERE work_programme = 'HORIZON_CL3_2021_BM_01';
UPDATE application_horizon_work_programme SET work_programme_id = 24 WHERE work_programme = 'HORIZON_CL3_2021_CS_01';
UPDATE application_horizon_work_programme SET work_programme_id = 25 WHERE work_programme = 'HORIZON_CL3_2021_DRS_01';
UPDATE application_horizon_work_programme SET work_programme_id = 26 WHERE work_programme = 'HORIZON_CL3_2021_FCT_01';
UPDATE application_horizon_work_programme SET work_programme_id = 27 WHERE work_programme = 'HORIZON_CL3_2021_INFRA_01';
UPDATE application_horizon_work_programme SET work_programme_id = 28 WHERE work_programme = 'HORIZON_CL3_2021_SSRI_01';
UPDATE application_horizon_work_programme SET work_programme_id = 29 WHERE work_programme = 'GRANTS_CL3';

UPDATE application_horizon_work_programme SET work_programme_id = 30 WHERE work_programme = 'HORIZON_CL4_2021_DATA_01';
UPDATE application_horizon_work_programme SET work_programme_id = 31 WHERE work_programme = 'HORIZON_CL4_2021_DIGITAL_EMERGING_01';
UPDATE application_horizon_work_programme SET work_programme_id = 32 WHERE work_programme = 'HORIZON_CL4_2021_DIGITAL_EMERGING_02';
UPDATE application_horizon_work_programme SET work_programme_id = 33 WHERE work_programme = 'HORIZON_CL4_2021_HUMAN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 34 WHERE work_programme = 'HORIZON_CL4_2021_RESILIENCE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 35 WHERE work_programme = 'HORIZON_CL4_2021_RESILIENCE_02';
UPDATE application_horizon_work_programme SET work_programme_id = 36 WHERE work_programme = 'HORIZON_CL4_2021_SPACE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 37 WHERE work_programme = 'HORIZON_CL4_2021_TWIN_TRANSITION_01';
UPDATE application_horizon_work_programme SET work_programme_id = 38 WHERE work_programme = 'HORIZON_EUSPA_2021_SPACE_02';
UPDATE application_horizon_work_programme SET work_programme_id = 39 WHERE work_programme = 'HORIZON_CL4_2022_DATA_01';
UPDATE application_horizon_work_programme SET work_programme_id = 40 WHERE work_programme = 'HORIZON_CL4_2022_DIGITAL_EMERGING_01';
UPDATE application_horizon_work_programme SET work_programme_id = 41 WHERE work_programme = 'HORIZON_CL4_2022_HUMAN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 42 WHERE work_programme = 'HORIZON_CL4_2022_RESILIENCE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 43 WHERE work_programme = 'HORIZON_CL4_2022_RESILIENCE_02_PCP';
UPDATE application_horizon_work_programme SET work_programme_id = 44 WHERE work_programme = 'HORIZON_CL4_2022_SPACE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 45 WHERE work_programme = 'HORIZON_CL4_2022_TWIN_TRANSITION_01';
UPDATE application_horizon_work_programme SET work_programme_id = 46 WHERE work_programme = 'GRANTS_CL4';

UPDATE application_horizon_work_programme SET work_programme_id = 47 WHERE work_programme = 'HORIZON_CL5_2021_D1_01';
UPDATE application_horizon_work_programme SET work_programme_id = 48 WHERE work_programme = 'HORIZON_CL5_2021_D2_01';
UPDATE application_horizon_work_programme SET work_programme_id = 49 WHERE work_programme = 'HORIZON_CL5_2021_D3_01';
UPDATE application_horizon_work_programme SET work_programme_id = 50 WHERE work_programme = 'HORIZON_CL5_2021_D3_02';
UPDATE application_horizon_work_programme SET work_programme_id = 51 WHERE work_programme = 'HORIZON_CL5_2021_D3_03';
UPDATE application_horizon_work_programme SET work_programme_id = 52 WHERE work_programme = 'HORIZON_CL5_2021_D4_01';
UPDATE application_horizon_work_programme SET work_programme_id = 53 WHERE work_programme = 'HORIZON_CL5_2021_D4_02';
UPDATE application_horizon_work_programme SET work_programme_id = 54 WHERE work_programme = 'HORIZON_CL5_2021_D5_01';
UPDATE application_horizon_work_programme SET work_programme_id = 55 WHERE work_programme = 'HORIZON_CL5_2021_D6_01';
UPDATE application_horizon_work_programme SET work_programme_id = 56 WHERE work_programme = 'HORIZON_CL5_2022_D1_02';
UPDATE application_horizon_work_programme SET work_programme_id = 57 WHERE work_programme = 'HORIZON_CL5_2022_D3_01';
UPDATE application_horizon_work_programme SET work_programme_id = 58 WHERE work_programme = 'HORIZON_CL5_2022_D5_01';
UPDATE application_horizon_work_programme SET work_programme_id = 59 WHERE work_programme = 'HORIZON_CL5_2022_D6_01';
UPDATE application_horizon_work_programme SET work_programme_id = 60 WHERE work_programme = 'GRANTS_CL5';

UPDATE application_horizon_work_programme SET work_programme_id = 61 WHERE work_programme = 'HORIZON_CL6_2021_BIODIV_01';
UPDATE application_horizon_work_programme SET work_programme_id = 62 WHERE work_programme = 'HORIZON_CL6_2021_BIODIV_02';
UPDATE application_horizon_work_programme SET work_programme_id = 63 WHERE work_programme = 'HORIZON_CL6_2021_CIRCBIO_01';
UPDATE application_horizon_work_programme SET work_programme_id = 64 WHERE work_programme = 'HORIZON_CL6_2021_CLIMATE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 65 WHERE work_programme = 'HORIZON_CL6_2021_COMMUNITIES_01';
UPDATE application_horizon_work_programme SET work_programme_id = 66 WHERE work_programme = 'HORIZON_CL6_2021_FARM2FORK_01';
UPDATE application_horizon_work_programme SET work_programme_id = 67 WHERE work_programme = 'HORIZON_CL6_2021_GOVERNANCE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 68 WHERE work_programme = 'HORIZON_CL6_2021_ZEROPOLLUTION_01';
UPDATE application_horizon_work_programme SET work_programme_id = 69 WHERE work_programme = 'HORIZON_CL6_2022_BIODIV_01';
UPDATE application_horizon_work_programme SET work_programme_id = 70 WHERE work_programme = 'HORIZON_CL6_2022_CIRCBIO_01';
UPDATE application_horizon_work_programme SET work_programme_id = 71 WHERE work_programme = 'HORIZON_CL6_2022_CLIMATE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 72 WHERE work_programme = 'HORIZON_CL6_2022_COMMUNITIES_01';
UPDATE application_horizon_work_programme SET work_programme_id = 73 WHERE work_programme = 'HORIZON_CL6_2022_FARM2FORK_01';
UPDATE application_horizon_work_programme SET work_programme_id = 74 WHERE work_programme = 'HORIZON_CL6_2022_GOVERNANCE_01';
UPDATE application_horizon_work_programme SET work_programme_id = 75 WHERE work_programme = 'HORIZON_CL6_2022_ZEROPOLLUTION_01';
UPDATE application_horizon_work_programme SET work_programme_id = 76 WHERE work_programme = 'GRANTS_CL6';

UPDATE application_horizon_work_programme SET work_programme_id = 77 WHERE work_programme = 'HORIZON_EIC_2021_EEN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 78 WHERE work_programme = 'HORIZON_EIC_2021_ACCELERATORCHALLENGES_01';
UPDATE application_horizon_work_programme SET work_programme_id = 79 WHERE work_programme = 'HORIZON_EIC_2021_ACCELERATOROPEN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 80 WHERE work_programme = 'HORIZON_EIC_2021_NCP_01';
UPDATE application_horizon_work_programme SET work_programme_id = 81 WHERE work_programme = 'HORIZON_EIC_2021_PATHFINDERCHALLENGES_01';
UPDATE application_horizon_work_programme SET work_programme_id = 82 WHERE work_programme = 'HORIZON_EIC_2021_PATHFINDEROPEN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 83 WHERE work_programme = 'HORIZON_EIC_2021_TRANSITIONCHALLENGES_01';
UPDATE application_horizon_work_programme SET work_programme_id = 84 WHERE work_programme = 'HORIZON_EIC_2021_TRANSITIONOPEN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 85 WHERE work_programme = 'GRANTS_EIC';

UPDATE application_horizon_work_programme SET work_programme_id = 86 WHERE work_programme = 'HORIZON_EIE_2021_CONNECT_01';
UPDATE application_horizon_work_programme SET work_programme_id = 87 WHERE work_programme = 'HORIZON_EIE_2021_INNOVSMES_01';
UPDATE application_horizon_work_programme SET work_programme_id = 88 WHERE work_programme = 'HORIZON_EIE_2021_SCALEUP_01';
UPDATE application_horizon_work_programme SET work_programme_id = 89 WHERE work_programme = 'HORIZON_EIE_2022_CONNECT_01';
UPDATE application_horizon_work_programme SET work_programme_id = 90 WHERE work_programme = 'GRANTS_EIE';

UPDATE application_horizon_work_programme SET work_programme_id = 91 WHERE work_programme = 'HORIZON_HLTH_2021_CARE_05';
UPDATE application_horizon_work_programme SET work_programme_id = 92 WHERE work_programme = 'HORIZON_HLTH_2021_DISEASE_04';
UPDATE application_horizon_work_programme SET work_programme_id = 93 WHERE work_programme = 'HORIZON_HLTH_2021_ENVHLTH_02';
UPDATE application_horizon_work_programme SET work_programme_id = 94 WHERE work_programme = 'HORIZON_HLTH_2021_ENVHLTH_03';
UPDATE application_horizon_work_programme SET work_programme_id = 95 WHERE work_programme = 'HORIZON_HLTH_2021_IND_07';
UPDATE application_horizon_work_programme SET work_programme_id = 96 WHERE work_programme = 'HORIZON_HLTH_2021_STAYHLTH_01';
UPDATE application_horizon_work_programme SET work_programme_id = 97 WHERE work_programme = 'HORIZON_HLTH_2021_TOOL_06';
UPDATE application_horizon_work_programme SET work_programme_id = 98 WHERE work_programme = 'HORIZON_HLTH_2022_CARE_08';
UPDATE application_horizon_work_programme SET work_programme_id = 99 WHERE work_programme = 'HORIZON_HLTH_2022_CARE_10';
UPDATE application_horizon_work_programme SET work_programme_id = 100 WHERE work_programme = 'HORIZON_HLTH_2022_DISEASE_03';
UPDATE application_horizon_work_programme SET work_programme_id = 101 WHERE work_programme = 'HORIZON_HLTH_2022_DISEASE_07';
UPDATE application_horizon_work_programme SET work_programme_id = 102 WHERE work_programme = 'HORIZON_HLTH_2022_ENVHLTH_04';
UPDATE application_horizon_work_programme SET work_programme_id = 103 WHERE work_programme = 'HORIZON_HLTH_2022_IND_13';
UPDATE application_horizon_work_programme SET work_programme_id = 104 WHERE work_programme = 'HORIZON_HLTH_2022_STAYHLTH_02';
UPDATE application_horizon_work_programme SET work_programme_id = 105 WHERE work_programme = 'HORIZON_HLTH_2022_TOOL_11';
UPDATE application_horizon_work_programme SET work_programme_id = 106 WHERE work_programme = 'GRANTS_HLTH';

UPDATE application_horizon_work_programme SET work_programme_id = 107 WHERE work_programme = 'HORIZON_INFRA_2021_DEV_01';
UPDATE application_horizon_work_programme SET work_programme_id = 108 WHERE work_programme = 'HORIZON_INFRA_2021_DEV_02';
UPDATE application_horizon_work_programme SET work_programme_id = 109 WHERE work_programme = 'HORIZON_INFRA_2021_EOSC_01';
UPDATE application_horizon_work_programme SET work_programme_id = 110 WHERE work_programme = 'HORIZON_INFRA_2021_NET_01_FPA';
UPDATE application_horizon_work_programme SET work_programme_id = 111 WHERE work_programme = 'HORIZON_INFRA_2021_SERV_01';
UPDATE application_horizon_work_programme SET work_programme_id = 112 WHERE work_programme = 'HORIZON_INFRA_2021_TECH_01';
UPDATE application_horizon_work_programme SET work_programme_id = 113 WHERE work_programme = 'HORIZON_INFRA_2022_DEV_01';
UPDATE application_horizon_work_programme SET work_programme_id = 114 WHERE work_programme = 'HORIZON_INFRA_2022_EOSC_01';
UPDATE application_horizon_work_programme SET work_programme_id = 115 WHERE work_programme = 'HORIZON_INFRA_2022_TECH_01';
UPDATE application_horizon_work_programme SET work_programme_id = 116 WHERE work_programme = 'GRANTS_INFRA';

UPDATE application_horizon_work_programme SET work_programme_id = 117 WHERE work_programme = 'HORIZON_MISS_2021_CIT_01';
UPDATE application_horizon_work_programme SET work_programme_id = 118 WHERE work_programme = 'HORIZON_MISS_2021_CLIMA_01';
UPDATE application_horizon_work_programme SET work_programme_id = 119 WHERE work_programme = 'HORIZON_MISS_2021_COOR_01';
UPDATE application_horizon_work_programme SET work_programme_id = 120 WHERE work_programme = 'HORIZON_MISS_2021_NEB_01';
UPDATE application_horizon_work_programme SET work_programme_id = 121 WHERE work_programme = 'HORIZON_MISS_2021_OCEAN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 122 WHERE work_programme = 'HORIZON_MISS_2021_SOIL_01';
UPDATE application_horizon_work_programme SET work_programme_id = 123 WHERE work_programme = 'HORIZON_MISS_2021_SOIL_02';
UPDATE application_horizon_work_programme SET work_programme_id = 124 WHERE work_programme = 'HORIZON_MISS_2021_UNCAN_01';
UPDATE application_horizon_work_programme SET work_programme_id = 125 WHERE work_programme = 'GRANTS_MISS';

UPDATE application_horizon_work_programme SET work_programme_id = 126 WHERE work_programme = 'HORIZON_WIDERA_2021_ACCESS_02';
UPDATE application_horizon_work_programme SET work_programme_id = 127 WHERE work_programme = 'HORIZON_WIDERA_2021_ACCESS_03';
UPDATE application_horizon_work_programme SET work_programme_id = 128 WHERE work_programme = 'HORIZON_WIDERA_2021_ACCESS_05';
UPDATE application_horizon_work_programme SET work_programme_id = 129 WHERE work_programme = 'HORIZON_WIDERA_2021_ACCESS_06';
UPDATE application_horizon_work_programme SET work_programme_id = 130 WHERE work_programme = 'HORIZON_WIDERA_2021_ERA_01';
UPDATE application_horizon_work_programme SET work_programme_id = 131 WHERE work_programme = 'HORIZON_WIDERA_2022_ACCESS_04';
UPDATE application_horizon_work_programme SET work_programme_id = 132 WHERE work_programme = 'HORIZON_WIDERA_2022_ACCESS_07';
UPDATE application_horizon_work_programme SET work_programme_id = 133 WHERE work_programme = 'HORIZON_WIDERA_2022_ERA_01';
UPDATE application_horizon_work_programme SET work_programme_id = 134 WHERE work_programme = 'HORIZON_WIDERA_2022_TALENTS_01';
UPDATE application_horizon_work_programme SET work_programme_id = 135 WHERE work_programme = 'HORIZON_WIDERA_2022_TALENTS_02';
UPDATE application_horizon_work_programme SET work_programme_id = 136 WHERE work_programme = 'HORIZON_WIDERA_2022_TALENTS_04';
UPDATE application_horizon_work_programme SET work_programme_id = 137 WHERE work_programme = 'GRANTS_WIDERA';

UPDATE application_horizon_work_programme SET work_programme_id = 138 WHERE work_programme = 'HORIZON_EURATOM_2021_NRT_01_01';
UPDATE application_horizon_work_programme SET work_programme_id = 139 WHERE work_programme = 'GRANTS_EURATOM';

UPDATE application_horizon_work_programme SET work_programme_id = 140 WHERE work_programme = 'HORIZON_JU_SNS_2022_STREAM_A_01';
UPDATE application_horizon_work_programme SET work_programme_id = 141 WHERE work_programme = 'HORIZON_JU_SNS_2022_STREAM_B_01';
UPDATE application_horizon_work_programme SET work_programme_id = 142 WHERE work_programme = 'HORIZON_JU_SNS_2022_STREAM_C_01';
UPDATE application_horizon_work_programme SET work_programme_id = 143 WHERE work_programme = 'HORIZON_JU_SNS_2022_STREAM_CSA_01';
UPDATE application_horizon_work_programme SET work_programme_id = 144 WHERE work_programme = 'HORIZON_JU_SNS_2022_STREAM_CSA_02';
UPDATE application_horizon_work_programme SET work_programme_id = 145 WHERE work_programme = 'HORIZON_JU_SNS_2022_STREAM_D_01';
UPDATE application_horizon_work_programme SET work_programme_id = 146 WHERE work_programme = 'HORIZON_KDT_JU_1_IA_Focus_Topic_1';
UPDATE application_horizon_work_programme SET work_programme_id = 147 WHERE work_programme = 'HORIZON_KDT_JU_2021_1_IA';
UPDATE application_horizon_work_programme SET work_programme_id = 148 WHERE work_programme = 'HORIZON_KDT_JU_2021_2_RIA_Focus_Topic_11';
UPDATE application_horizon_work_programme SET work_programme_id = 149 WHERE work_programme = 'HORIZON_KDT_JU_2021_2_RIA';
UPDATE application_horizon_work_programme SET work_programme_id = 150 WHERE work_programme = 'HORIZON_KDT_JU_2021_3_CSA';

UPDATE application_horizon_work_programme SET work_programme_id = 151 WHERE work_programme = 'HORIZON_EUROPEAN_METROLOGY_PARTNERSHIP_2021_CALL';

ALTER TABLE application_horizon_work_programme DROP COLUMN work_programme;