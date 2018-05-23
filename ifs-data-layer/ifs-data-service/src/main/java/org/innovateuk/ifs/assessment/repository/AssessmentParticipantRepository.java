package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.competition.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentParticipantRepository extends CompetitionParticipantRepository<AssessmentParticipant> {

    String USERS_WITH_ASSESSMENT_PANEL_INVITE = "SELECT invite.user.id " +
            "FROM ReviewInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    String USERS_WITH_INTERVIEW_PANEL_INVITE = "SELECT invite.user.id " +
            "FROM InterviewInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    String PARTICIPANTS_NOT_ON_ASSESSMENT_PANEL = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED " +
            "AND assessmentParticipant.user.id NOT IN (" + USERS_WITH_ASSESSMENT_PANEL_INVITE + ")";

    String PARTICIPANTS_NOT_ON_INTERVIEW_PANEL = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED " +
            "AND assessmentParticipant.user.id NOT IN (" + USERS_WITH_INTERVIEW_PANEL_INVITE + ")";

    String BY_COMP_AND_STATUS = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.status IN :status";

    String BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "LEFT JOIN Profile profile ON profile.id = assessmentParticipant.user.profileId " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.status IN :status " +
            "AND (:innovationAreaId IS NULL " +
            "   OR EXISTS(" +
            "       SELECT profile.id " +
            "       FROM Profile profile " +
            "       JOIN profile.innovationAreas innovationAreas " +
            "       WHERE profile.id = assessmentParticipant.user.profileId " +
            "       AND innovationAreas.category.id = :innovationAreaId " +
            "   ) " +
            "   OR assessmentParticipant.invite.innovationArea.id = :innovationAreaId) " +
            "AND (:isCompliant IS NULL " +
            "   OR (:isCompliant = true AND (" +
            "       EXISTS(" +
            "           SELECT affiliation.id " +
            "           FROM Affiliation affiliation " +
            "           WHERE affiliation.user.id = assessmentParticipant.user.id " +
            "       ) " +
            "       AND profile.skillsAreas IS NOT NULL " +
            "       AND profile.agreement IS NOT NULL) " +
            "   OR (:isCompliant = false AND (" +
            "       NOT EXISTS(" +
            "           SELECT affiliation.id " +
            "           FROM Affiliation affiliation " +
            "           WHERE affiliation.user.id = assessmentParticipant.user.id " +
            "       ) " +
            "       OR profile.skillsAreas IS NULL " +
            "       OR profile.agreement IS NULL)" +
            "   )" +
            "))";

    String PARTICIPANTS_WITHOUT_ASSESSMENTS = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "WHERE assessmentParticipant.competition.id = :compId " +
            "AND assessmentParticipant.role = :role " +
            "AND assessmentParticipant.status = :status " +
            "AND NOT EXISTS (" +
            "   SELECT 'found' " +
            "   FROM Assessment a " +
            "   WHERE a.participant.user = assessmentParticipant.user " +
            "   AND a.target.id = :appId) " +
            "   AND (:innovationAreaId is null " +
            "       OR EXISTS (" +
            "           SELECT 'area' " +
            "           FROM Profile p " +
            "           JOIN p.innovationAreas ia " +
            "           WHERE p.id = assessmentParticipant.user.profileId " +
            "           AND ia.category.id = :innovationAreaId" +
            "       )" +
            ")";

    String PARTICIPANTS_WITH_ASSESSMENTS = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "WHERE assessmentParticipant.competition.id = :compId " +
            "AND assessmentParticipant.role = :role " +
            "AND assessmentParticipant.status = :status " +
            "AND EXISTS (" +
            "   SELECT 'found' " +
            "   FROM Assessment a " +
            "   WHERE a.participant.user = assessmentParticipant.user " +
            "   AND a.target.id = :appId" +
            ")";

    @Override
    List<AssessmentParticipant> findAll();

    AssessmentParticipant getByInviteHash(String hash);

    List<AssessmentParticipant> getByUserIdAndRole(Long userId, CompetitionParticipantRole role);

    List<AssessmentParticipant> getByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    @Query(BY_COMP_AND_STATUS)
    Page<AssessmentParticipant> getAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                           @Param("status") List<ParticipantStatus> status,
                                                                           Pageable pageable);

    @Query(BY_COMP_AND_STATUS)
    List<AssessmentParticipant> getAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                           @Param("status") List<ParticipantStatus> status);

    @Query(BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT)
    Page<AssessmentParticipant> getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(@Param("competitionId") long competitionId,
                                                                                                        @Param("innovationAreaId") Long innovationAreaId,
                                                                                                        @Param("status") List<ParticipantStatus> status,
                                                                                                        @Param("isCompliant") Boolean isCompliant,
                                                                                                        Pageable pageable);
    @Query(BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT)
    List<AssessmentParticipant> getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(@Param("competitionId") long competitionId,
                                                                                                        @Param("innovationAreaId") Long innovationAreaId,
                                                                                                        @Param("status") List<ParticipantStatus> status,
                                                                                                        @Param("isCompliant") Boolean isCompliant);

    List<AssessmentParticipant> getByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    List<AssessmentParticipant> getByInviteEmail(String email);

    AssessmentParticipant getByInviteId(long id);

    int countByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    int countByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    @Query(PARTICIPANTS_WITHOUT_ASSESSMENTS)
    Page<AssessmentParticipant> findParticipantsWithoutAssessments(@Param("compId") long competitionId,
                                                                   @Param("role") CompetitionParticipantRole role,
                                                                   @Param("status") ParticipantStatus status,
                                                                   @Param("appId") long applicationId,
                                                                   @Param("innovationAreaId") Long filterInnovationArea,
                                                                   Pageable pageable);

    @Query(PARTICIPANTS_WITH_ASSESSMENTS)
    List<AssessmentParticipant> findParticipantsWithAssessments(
            @Param("compId") long competitionId,
            @Param("role") CompetitionParticipantRole role,
            @Param("status") ParticipantStatus status,
            @Param("appId") long applicationId);

    @Query(PARTICIPANTS_NOT_ON_ASSESSMENT_PANEL)
    Page<AssessmentParticipant> findParticipantsNotOnAssessmentPanel(@Param("competitionId") long competitionId, Pageable pageable);

    @Query(PARTICIPANTS_NOT_ON_ASSESSMENT_PANEL)
    List<AssessmentParticipant> findParticipantsNotOnAssessmentPanel(@Param("competitionId") long competitionId);

    @Query(PARTICIPANTS_NOT_ON_INTERVIEW_PANEL)
    Page<AssessmentParticipant> findParticipantsNotOnInterviewPanel(@Param("competitionId") long competitionId, Pageable pageable);

    @Query(PARTICIPANTS_NOT_ON_INTERVIEW_PANEL)
    List<AssessmentParticipant> findParticipantsNotOnInterviewPanel(@Param("competitionId") long competitionId);
}
