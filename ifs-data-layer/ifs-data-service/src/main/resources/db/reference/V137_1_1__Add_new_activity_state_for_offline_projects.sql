 -- IFS-5110 Create a state for projects to be handled and completed offline
ALTER TABLE `activity_state` MODIFY COLUMN `state` enum('CREATED','PENDING','REJECTED','ACCEPTED','WITHDRAWN','OPEN','READY_TO_SUBMIT','SUBMITTED','VERIFIED','NOT_VERIFIED','ASSIGNED','NOT_ASSIGNED','NOT_APPLICABLE','NOT_APPLICABLE_INFORMED','CONFLICT_OF_INTEREST', 'HANDLED_OFFLINE', 'COMPLETED_OFFLINE') NOT NULL;

INSERT INTO `activity_state` (id, activity_type, state) VALUES (51, 'PROJECT_SETUP', 'HANDLED_OFFLINE');
INSERT INTO `activity_state` (id, activity_type, state) VALUES (52, 'PROJECT_SETUP', 'COMPLETED_OFFLINE');