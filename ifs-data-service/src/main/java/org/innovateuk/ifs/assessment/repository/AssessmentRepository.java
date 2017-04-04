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

    static final String FEEDBACK_COMPLETE = "SELECT CASE WHEN COUNT(formInput.id) = 0" +
            "  THEN TRUE" +
            "       ELSE FALSE END AS feedback_complete " +
            "FROM Application application" +
            "  INNER JOIN Competition competition" +
            "    ON application.competition.id = competition.id" +
            "  INNER JOIN Assessment assessment" +
            "    ON application.id = assessment.target.id" +
            "  INNER JOIN Question question" +
            "    ON question.competition.id = competition.id" +
            "  INNER JOIN FormInput formInput" +
            "    ON formInput.question.id = question.id " +
            "WHERE NOT EXISTS(" +
            "    SELECT 1 AS response" +
            "    FROM Assessment assessment" +
            "      INNER JOIN AssessorFormInputResponse assessorFormInputResponse" +
            "        ON assessorFormInputResponse.assessment.id = assessment.id" +
            "    WHERE assessorFormInputResponse.value IS NOT NULL" +
            "          AND assessorFormInputResponse.formInput.id = formInput.id" +
            "          AND assessment.id = :id" +
            ")" +
            "      AND formInput.scope = 'ASSESSMENT'" +
            "      AND formInput.active = TRUE" +
            "      AND assessment.id = :id";

    static final String TOTAL_SCORE = "SELECT NEW org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource(" +
            "  CAST(COALESCE(SUM(assessorFormInputResponse.value),0) AS int)," +
            "  CAST(SUM(question.assessorMaximumScore) AS int)) " +
            "FROM Assessment assessment" +
            "  JOIN assessment.target.competition.questions question" +
            "  JOIN question.formInputs formInput" +
            "  LEFT JOIN assessment.responses assessorFormInputResponse" +
            "    ON assessorFormInputResponse.formInput.id = formInput " +
            "WHERE formInput.type = org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE" +
            "  AND formInput.active = TRUE" +
            "  AND assessment.id = :id";

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
