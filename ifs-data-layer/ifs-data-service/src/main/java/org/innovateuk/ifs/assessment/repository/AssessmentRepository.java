package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
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

    String FEEDBACK_COMPLETE = "SELECT CASE WHEN COUNT(formInput.id) = 0" +
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

    String TOTAL_SCORE = "SELECT NEW org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource(" +
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

    List<Assessment> findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(Long userId, Long competitionId);

    List<Assessment> findByParticipantUserIdAndActivityStateNotIn(long userId, Collection<AssessmentState> states);

    List<Assessment> findByParticipantUserIdAndTargetId(long userId, long applicationId);

    Optional<Assessment> findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(Long userId, Long applicationId);

    long countByParticipantUserIdAndActivityStateIn(Long userId, Set<AssessmentState> states);

    long countByParticipantUserIdAndActivityStateNotIn(Long userId, Set<AssessmentState> states);

    long countByParticipantUserIdAndTargetCompetitionIdAndActivityStateIn(Long userId, Long competitionId, Set<AssessmentState> states);

    List<Assessment> findByActivityStateAndTargetCompetitionId(AssessmentState state, long competitionId);

    @Query("SELECT new org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount( " +
            "   assessment, " +
            "   application, " +
            "   CAST((SELECT COUNT(otherAssessment) " +
            "   FROM Assessment otherAssessment " +
            "   WHERE otherAssessment.target.id = application.id " +
            "       AND otherAssessment.activityState IN :validStates " +
            "   GROUP BY otherAssessment.target " +
            "   ) as int)" +
            ") " +
            "FROM Assessment assessment " +
            "JOIN assessment.target application " +
            "JOIN assessment.target.competition competition " +
            "JOIN assessment.participant participant " +
            "WHERE competition.id = :competitionId " +
            "   AND participant.user.id = :assessorId " +
            "   AND assessment.activityState IN :allStates " +
            "ORDER BY assessment.id DESC")
    List<AssessmentApplicationAssessorCount> getAssessorApplicationAssessmentCountsForStates(
            @Param("competitionId") long competitionId,
            @Param("assessorId") long assessorId,
            @Param("validStates") Collection<AssessmentState> validStates,
            @Param("allStates") Collection<AssessmentState> allStates
    );

    int countByActivityStateAndTargetCompetitionId(AssessmentState state, Long competitionId);

    int countByActivityStateInAndTargetCompetitionId(Collection<AssessmentState> state, Long competitionId);

    @Query(FEEDBACK_COMPLETE)
    boolean isFeedbackComplete(@Param("id") Long id);

    @Query(TOTAL_SCORE)
    AssessmentTotalScoreResource getTotalScore(@Param("id") Long id);
}
