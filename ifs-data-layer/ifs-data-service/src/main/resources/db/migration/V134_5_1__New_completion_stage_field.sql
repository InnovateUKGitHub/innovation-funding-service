-- IFS-4650 New field to indicate when the Competition is considered closed

ALTER TABLE competition ADD completion_stage ENUM('COMPETITION_CLOSE','RELEASE_FEEDBACK','PROJECT_SETUP') NULL;