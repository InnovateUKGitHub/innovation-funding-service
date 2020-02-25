package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;

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
            "JOIN assessmentParticipant.user.roleProfileStatuses roleStatuses " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED " +
            "AND assessmentParticipant.user.id NOT IN (" + USERS_WITH_ASSESSMENT_PANEL_INVITE + ")" +
            "AND roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE ";

    String PARTICIPANTS_NOT_ON_INTERVIEW_PANEL = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "JOIN assessmentParticipant.user.roleProfileStatuses roleStatuses " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED " +
            "AND assessmentParticipant.user.id NOT IN (" + USERS_WITH_INTERVIEW_PANEL_INVITE + ")" +
            "AND roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE ";

    String BY_COMP_AND_STATUS_AND_NAME = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "LEFT JOIN assessmentParticipant.user.roleProfileStatuses roleStatuses " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.status IN :status " +
            "AND assessmentParticipant.invite.name LIKE CONCAT('%', :assessorName, '%')" +
            " AND (roleStatuses IS NULL OR " +
            "(" +
            "    roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE " +
            "))";

    String BY_STATUS_AND_COMPLIANT_AND_NAME = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "LEFT JOIN assessmentParticipant.user.roleProfileStatuses roleStatuses " +
            "LEFT JOIN Profile profile ON profile.id = assessmentParticipant.user.profileId " +
            "WHERE assessmentParticipant.competition.id = :competitionId " +
            "AND assessmentParticipant.role = 'ASSESSOR' " +
            "AND assessmentParticipant.invite.name LIKE CONCAT('%', :assessorName, '%') " +
            "AND assessmentParticipant.status IN :status " +
            "AND (:isCompliant IS NULL " +
            "   OR (:isCompliant = true AND (" +
            "       EXISTS(" +
            "           SELECT affiliation.id " +
            "           FROM Affiliation affiliation " +
            "           WHERE affiliation.user.id = assessmentParticipant.user.id " +
            "           AND affiliation.modifiedOn > :startOfTaxYear " +
            "       ) " +
            "       AND profile.skillsAreas IS NOT NULL " +
            "       AND profile.agreement IS NOT NULL) " +
            "   OR (:isCompliant = false AND (" +
            "       NOT EXISTS(" +
            "           SELECT affiliation.id " +
            "           FROM Affiliation affiliation " +
            "           WHERE affiliation.user.id = assessmentParticipant.user.id " +
            "           AND affiliation.modifiedOn > :startOfTaxYear " +
            "       ) " +
            "       OR profile.skillsAreas IS NULL " +
            "       OR profile.agreement IS NULL)" +
            "   )" +
            "))" +
            " AND (roleStatuses IS NULL OR " +
            "(" +
            "    roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE " +
            "))";

    String PARTICIPANTS_WITHOUT_ASSESSMENTS = "SELECT assessmentParticipant " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "JOIN assessmentParticipant.user.roleProfileStatuses roleStatuses " +
            "WHERE assessmentParticipant.competition.id = :compId " +
            "AND assessmentParticipant.role = :role " +
            "AND assessmentParticipant.status = :status " +
            "AND NOT EXISTS (" +
            "   SELECT 'found' " +
            "   FROM Assessment a " +
            "   WHERE a.participant.user = assessmentParticipant.user " +
            "   AND a.target.id = :appId) " +
            "AND roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE " +
            "AND CONCAT(assessmentParticipant.user.firstName, ' ', assessmentParticipant.user.lastName) LIKE CONCAT('%', :assessorNameFilter, '%')";

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

    default List<AssessmentParticipant> getByAssessorId(long assessorId) {
        return getByUserIdAndRole(assessorId, ASSESSOR);
    }

    List<AssessmentParticipant> getByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    @Query(BY_COMP_AND_STATUS_AND_NAME)
    Page<AssessmentParticipant> getAssessorsByCompetitionAndStatusContainsAndAssessorNameLike(long competitionId,
                                                                                              List<ParticipantStatus> status,
                                                                                              String assessorName,
                                                                                              Pageable pageable);

    @Query(BY_COMP_AND_STATUS_AND_NAME)
    List<AssessmentParticipant> getAssessorsByCompetitionAndStatusContainsAndAssessorNameLike(long competitionId,
                                                                                              List<ParticipantStatus> status,
                                                                                              String assessorName);

    @Query(BY_STATUS_AND_COMPLIANT_AND_NAME)
    Page<AssessmentParticipant> getAssessorsByCompetitionAndStatusContainsAndCompliantAndAssessorNameLike(long competitionId,
                                                                                                          List<ParticipantStatus> status,
                                                                                                          Boolean isCompliant,
                                                                                                          String assessorName,
                                                                                                          ZonedDateTime startOfTaxYear,
                                                                                                          Pageable pageable);

    @Query(BY_STATUS_AND_COMPLIANT_AND_NAME)
    List<AssessmentParticipant> getAssessorsByCompetitionAndStatusContainsAndCompliantAndAssessorNameLike(long competitionId,
                                                                                                          List<ParticipantStatus> status,
                                                                                                          Boolean isCompliant,
                                                                                                          String assessorName,
                                                                                                          ZonedDateTime startOfTaxYear);

    List<AssessmentParticipant> getByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    List<AssessmentParticipant> getByInviteEmail(String email);

    AssessmentParticipant getByInviteId(long id);

    int countByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    @Query("SELECT count(participant) " +
            "FROM AssessmentParticipant participant " +
            "LEFT JOIN participant.user.roleProfileStatuses roleStatuses " +
            "WHERE participant.role = :role " +
            " AND participant.competition.id = :competitionId " +
            " AND participant.status = :status " +
            " AND (roleStatuses IS NULL OR " +
            "(" +
            "    roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            " AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE " +
            "))")
    int countByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    @Query(PARTICIPANTS_WITHOUT_ASSESSMENTS)
    Page<AssessmentParticipant> findParticipantsWithoutAssessments(@Param("compId") long competitionId,
                                                                   @Param("role") CompetitionParticipantRole role,
                                                                   @Param("status") ParticipantStatus status,
                                                                   @Param("appId") long applicationId,
                                                                   @Param("assessorNameFilter") String assessorNameFilter,
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

    void deleteByCompetitionIdAndRole(long competitionId, CompetitionParticipantRole competitionParticipantRole);
}
