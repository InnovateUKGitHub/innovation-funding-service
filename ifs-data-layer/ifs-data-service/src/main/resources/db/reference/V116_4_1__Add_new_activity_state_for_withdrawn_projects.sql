 -- Create a backing state for withdrawn projects, to be used for state machine project processes: IFS-2992
INSERT INTO `activity_state` (activity_type, state) VALUES ('PROJECT_SETUP', 'WITHDRAWN');