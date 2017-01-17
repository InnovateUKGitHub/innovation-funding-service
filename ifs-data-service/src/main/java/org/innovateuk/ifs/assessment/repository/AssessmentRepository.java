package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends ProcessRepository<Assessment>, PagingAndSortingRepository<Assessment, Long> {

    @Override
    Set<Assessment> findAll();

    @Override
    List<Assessment> findAll(Iterable<Long> assessmentIds);

    @Override
    Assessment findOneByParticipantId(Long participantId);

    List<Assessment> findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(Long userId, Long competitionId);

    List<Assessment> findByActivityStateStateAndTargetCompetitionId(State state, Long competitionId);

    @Query(value = "SELECT CASE WHEN COUNT(fi.id) = 0" +
            "  THEN 'TRUE'" +
            "       ELSE 'FALSE' END AS feedback_complete " +
            "FROM application a" +
            "  INNER JOIN competition c" +
            "    ON a.competition = c.id" +
            "  INNER JOIN process p" +
            "    ON a.id = p.target_id" +
            "  INNER JOIN question q" +
            "    ON q.competition_id = c.id" +
            "  INNER JOIN form_input fi" +
            "    ON fi.question_id = q.id " +
            "WHERE NOT EXISTS(" +
            "    SELECT 1 AS response" +
            "    FROM process p" +
            "      INNER JOIN assessor_form_input_response afir" +
            "        ON afir.assessment_id = p.id" +
            "    WHERE afir.value IS NOT NULL" +
            "          AND afir.form_input_id = fi.id" +
            "          AND p.id = :id" +
            ")" +
            "      AND fi.scope = 'ASSESSMENT'" +
            "      AND fi.active = TRUE" +
            "      AND p.id = :id", nativeQuery = true)
    boolean isFeedbackComplete(@Param("id") Long id);

    @Query(value = "SELECT NEW org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource(" +
            "  CAST(COALESCE(SUM(afir.value),0) AS int)," +
            "  CAST(SUM(q.assessorMaximumScore) AS int)) " +
            "FROM Assessment a" +
            "  JOIN a.target.competition.questions q" +
            "  JOIN q.formInputs fi" +
            "  LEFT JOIN a.responses afir" +
            "    ON afir.formInput.id = fi " +
            "WHERE fi.type = org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE" +
            "  AND fi.active = TRUE" +
            "  AND a.id = :id")
    AssessmentTotalScoreResource getTotalScore(@Param("id") Long id);
}
