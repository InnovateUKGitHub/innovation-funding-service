-- IFS-7313 add new completion_stage for competitions
ALTER TABLE competition
    MODIFY COLUMN completion_stage enum('COMPETITION_CLOSE',
                            'RELEASE_FEEDBACK',
                            'PROJECT_SETUP') NULL;