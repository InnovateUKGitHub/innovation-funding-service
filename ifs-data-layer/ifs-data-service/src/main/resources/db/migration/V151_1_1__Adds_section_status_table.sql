CREATE TABLE section_status (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT(20),
    section_id BIGINT(20),
    marked_as_complete BOOLEAN DEFAULT FALSE,
    -- do we care who marked as complete? it's going to be tricky to migrate
    marked_as_complete_by_id BIGINT(20),
    marked_as_complete_on DATETIME,

    UNIQUE KEY section_status_application_section_unique (application_id, section_id),

    CONSTRAINT section_status_application_id_fk FOREIGN KEY (application_id) REFERENCES application (id),
    CONSTRAINT section_status_section_fk FOREIGN KEY (section_id) REFERENCES section (id),
    CONSTRAINT section_status_marked_as_complete_by_fk FOREIGN KEY (marked_as_complete_by_id) REFERENCES process_role (id)
);

DELIMITER //

-- questions are all marked as complete
-- child sections are all marked as complete
-- can't use recursive functions in mysql!
-- but to migrate we only need to go one level deep
-- logic is pretty much SectionStatusServiceImpl.isSectionComplete
-- TODO isFinanceOverviewComplete logic

CREATE FUNCTION section_complete(application_id  BIGINT(20), section_id BIGINT(20))
    RETURNS BOOLEAN
BEGIN

    IF EXISTS (SELECT 1 FROM question_status qs
               INNER JOIN question q ON qs.question_id = q.id
               WHERE qs.application_id = application_id AND q.section_id = section_id AND NOT qs.marked_as_complete) THEN
      RETURN FALSE;
    END IF;

    -- next check child section questions
    IF EXISTS (SELECT 1 FROM question_status qs
               INNER JOIN question q ON qs.question_id = q.id
               INNER JOIN section s ON s.id = q.section_id
               WHERE s.parent_section_id = section_id AND NOT qs.marked_as_complete) THEN
      RETURN FALSE;
    END IF;

    RETURN TRUE;

END //

DELIMITER ;

-- writing a migration script for this isn't going to be nice
-- insert into section_status

INSERT INTO section_status (application_id, section_id, marked_as_complete)
    SELECT
        a.id                                                AS application_id,
        s.id                                                AS section_id,
        section_complete(a.id, s.id)  AS section_complete
           -- who marked this section as complete? do we actually care?
    FROM application a
    INNER JOIN section s ON s.competition_id = a.competition;

DROP FUNCTION section_complete;
