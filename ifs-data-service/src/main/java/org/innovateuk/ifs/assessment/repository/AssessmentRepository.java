package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentRepository extends ProcessRepository<Assessment>, PagingAndSortingRepository<Assessment, Long> {

    static final String FEEDBACK_COMPLETE = "SELECT CASE WHEN COUNT(fi.id) = 0" +
            "  THEN 'TRUE'" +
            "       ELSE 'FALSE' END AS feedback_complete " +
            "FROM Application a" +
            "  INNER JOIN Competition c" +
            "    ON a.competition.id = c.id" +
            "  INNER JOIN Assessment p" +
            "    ON a.id = p.target.id" +
            "  INNER JOIN Question q" +
            "    ON q.competition.id = c.id" +
            "  INNER JOIN FormInput fi" +
            "    ON fi.question.id = q.id " +
            "WHERE NOT EXISTS(" +
            "    SELECT 1 AS response" +
            "    FROM Assessment p" +
            "      INNER JOIN AssessorFormInputResponse afir" +
            "        ON afir.assessment.id = p.id" +
            "    WHERE afir.value IS NOT NULL" +
            "          AND afir.formInput.id = fi.id" +
            "          AND p.id = :id" +
            ")" +
            "      AND fi.scope = 'ASSESSMENT'" +
            "      AND fi.active = TRUE" +
            "      AND p.id = :id";

    static final String TOTAL_SCORE = "SELECT NEW org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource(" +
            "  CAST(COALESCE(SUM(afir.value),0) AS int)," +
            "  CAST(SUM(q.assessorMaximumScore) AS int)) " +
            "FROM Assessment a" +
            "  JOIN a.target.competition.questions q" +
            "  JOIN q.formInputs fi" +
            "  LEFT JOIN a.responses afir" +
            "    ON afir.formInput.id = fi " +
            "WHERE fi.type = org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE" +
            "  AND fi.active = TRUE" +
            "  AND a.id = :id";

    @Override
    Set<Assessment> findAll();

    @Override
    List<Assessment> findAll(Iterable<Long> assessmentIds);

    @Override
    Assessment findOneByParticipantId(Long participantId);

    List<Assessment> findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(Long userId, Long competitionId);

    Optional<Assessment> findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(Long userId, Long applicationId);

    long countByParticipantUserIdAndActivityStateStateNotIn(Long userId, Set<State> states);

    long countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(Long userId, Long competitionId, Set<State> states);

    List<Assessment> findByActivityStateStateAndTargetCompetitionId(State state, long competitionId);

    int countByActivityStateStateAndTargetCompetitionId(State state, Long competitionId);

    int countByActivityStateStateInAndTargetCompetitionId(Collection<State> state, Long competitionId);

    @Query(FEEDBACK_COMPLETE)
    boolean isFeedbackComplete(@Param("id") Long id);

    @Query(TOTAL_SCORE)
    AssessmentTotalScoreResource getTotalScore(@Param("id") Long id);
}
