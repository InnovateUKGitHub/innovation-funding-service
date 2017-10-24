package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.CompetitionAssessmentParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionParticipantRepository extends PagingAndSortingRepository<CompetitionAssessmentParticipant, Long> {

    String USERS_WITH_ASSESSMENT_PANEL_INVITE = "SELECT invite.user.id " +
            "FROM AssessmentPanelInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    String PARTICIPANTS_NOT_ON_PANEL = "SELECT competitionParticipant " +
            "FROM CompetitionAssessmentParticipant competitionParticipant " +
            "WHERE competitionParticipant.competition.id = :competitionId " +
            "AND competitionParticipant.role = 'ASSESSOR' " +
            "AND competitionParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED " +
            "AND competitionParticipant.user.id NOT IN (" + USERS_WITH_ASSESSMENT_PANEL_INVITE + ")";

    String BY_COMP_AND_STATUS = "SELECT competitionParticipant " +
            "FROM CompetitionAssessmentParticipant competitionParticipant " +
            "WHERE competitionParticipant.competition.id = :competitionId " +
            "AND competitionParticipant.role = 'ASSESSOR' " +
            "AND competitionParticipant.status IN :status";

    String BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT = "SELECT competitionParticipant " +
            "FROM CompetitionAssessmentParticipant competitionParticipant " +
            "LEFT JOIN Profile profile ON profile.id = competitionParticipant.user.profileId " +
            "WHERE competitionParticipant.competition.id = :competitionId " +
            "AND competitionParticipant.role = 'ASSESSOR' " +
            "AND competitionParticipant.status IN :status " +
            "AND (:innovationAreaId IS NULL " +
            "   OR EXISTS(" +
            "       SELECT profile.id " +
            "       FROM Profile profile " +
            "       JOIN profile.innovationAreas innovationAreas " +
            "       WHERE profile.id = competitionParticipant.user.profileId " +
            "       AND innovationAreas.category.id = :innovationAreaId " +
            "   ) " +
            "   OR competitionParticipant.invite.innovationArea.id = :innovationAreaId) " +
            "AND (:isCompliant IS NULL " +
            "   OR (:isCompliant = true AND (" +
            "       EXISTS(" +
            "           SELECT affiliation.id " +
            "           FROM Affiliation affiliation " +
            "           WHERE affiliation.user.id = competitionParticipant.user.id " +
            "       ) " +
            "       AND profile.skillsAreas IS NOT NULL " +
            "       AND profile.agreement IS NOT NULL) " +
            "   OR (:isCompliant = false AND (" +
            "       NOT EXISTS(" +
            "           SELECT affiliation.id " +
            "           FROM Affiliation affiliation " +
            "           WHERE affiliation.user.id = competitionParticipant.user.id " +
            "       ) " +
            "       OR profile.skillsAreas IS NULL " +
            "       OR profile.agreement IS NULL)" +
            "   )" +
            "))";

    String PARTICIPANTS_WITHOUT_ASSESSMENTS = "SELECT cp FROM CompetitionAssessmentParticipant cp WHERE cp.competition.id = :compId " +
            "AND cp.role = :role " +
            "AND cp.status = :status " +
            "AND NOT EXISTS " +
            "(SELECT 'found' FROM Assessment a WHERE " +
            "a.participant.user = cp.user " +
            "AND a.target.id = :appId) " +
            "AND (:innovationAreaId is null OR EXISTS " +
            "(SELECT 'area' FROM Profile p JOIN p.innovationAreas ia WHERE " +
            "p.id = cp.user.profileId " +
            "AND ia.category.id = :innovationAreaId ))";

    String PARTICIPANTS_WITH_ASSESSMENTS = "SELECT cp FROM CompetitionAssessmentParticipant cp WHERE " +
            "cp.competition.id = :compId " +
            "AND cp.role = :role " +
            "AND cp.status = :status " +
            "AND EXISTS (SELECT 'found' FROM Assessment a WHERE a.participant.user = cp.user AND a.target.id = :appId)";

    @Override
    List<CompetitionAssessmentParticipant> findAll();

    CompetitionAssessmentParticipant getByInviteHash(String hash);

    List<CompetitionAssessmentParticipant> getByUserIdAndRole(Long userId, CompetitionParticipantRole role);

    List<CompetitionAssessmentParticipant> getByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    CompetitionAssessmentParticipant getByCompetitionIdAndUserIdAndRole(Long competitionId, Long userId, CompetitionParticipantRole role);

    @Query(BY_COMP_AND_STATUS)
    Page<CompetitionAssessmentParticipant> getAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                    @Param("status") List<ParticipantStatus> status,
                                                                    Pageable pageable);

    @Query(BY_COMP_AND_STATUS)
    List<CompetitionAssessmentParticipant> getAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                            @Param("status") List<ParticipantStatus> status);

    @Query(BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT)
    Page<CompetitionAssessmentParticipant> getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(@Param("competitionId") long competitionId,
                                                                                                 @Param("innovationAreaId") Long innovationAreaId,
                                                                                                 @Param("status") List<ParticipantStatus> status,
                                                                                                 @Param("isCompliant") Boolean isCompliant,
                                                                                                 Pageable pageable);
    @Query(BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT)
    List<CompetitionAssessmentParticipant> getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(@Param("competitionId") long competitionId,
                                                                                                         @Param("innovationAreaId") Long innovationAreaId,
                                                                                                         @Param("status") List<ParticipantStatus> status,
                                                                                                         @Param("isCompliant") Boolean isCompliant);

    List<CompetitionAssessmentParticipant> getByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    List<CompetitionAssessmentParticipant> getByInviteEmail(String email);

    CompetitionAssessmentParticipant getByInviteId(long id);

    int countByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    int countByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    int countByCompetitionIdAndRoleAndStatusAndInviteIdIn(long competitionId,
                                                          CompetitionParticipantRole role,
                                                          ParticipantStatus status,
                                                          List<Long> inviteIds);

    @Query(PARTICIPANTS_WITHOUT_ASSESSMENTS)
    Page<CompetitionAssessmentParticipant> findParticipantsWithoutAssessments(@Param("compId") long competitionId,
                                                                    @Param("role") CompetitionParticipantRole role,
                                                                    @Param("status") ParticipantStatus status,
                                                                    @Param("appId") long applicationId,
                                                                    @Param("innovationAreaId") Long filterInnovationArea,
                                                                    Pageable pageable);

    @Query(PARTICIPANTS_WITH_ASSESSMENTS)
    List<CompetitionAssessmentParticipant> findParticipantsWithAssessments(
            @Param("compId") long competitionId,
            @Param("role") CompetitionParticipantRole role,
            @Param("status") ParticipantStatus status,
            @Param("appId") long applicationId);

    @Query(PARTICIPANTS_NOT_ON_PANEL)
    Page<CompetitionAssessmentParticipant> findParticipantsNotOnPanel(@Param("competitionId") long competitionId, Pageable pageable);

    @Query(PARTICIPANTS_NOT_ON_PANEL)
    List<CompetitionAssessmentParticipant> findParticipantsNotOnPanel(@Param("competitionId") long competitionId);
}
