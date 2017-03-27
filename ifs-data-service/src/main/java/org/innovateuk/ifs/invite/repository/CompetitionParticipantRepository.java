package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
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
public interface CompetitionParticipantRepository extends PagingAndSortingRepository<CompetitionParticipant, Long> {

    static final String BY_COMP_AND_STATUS = "SELECT competitionParticipant " +
            "FROM CompetitionParticipant competitionParticipant " +
            "WHERE competitionParticipant.competition.id = :competitionId " +
            "AND competitionParticipant.role = 'ASSESSOR' " +
            "AND (:status IS NULL OR competitionParticipant.status = :status)";

    static final String BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT = "SELECT competitionParticipant " +
            "FROM CompetitionParticipant competitionParticipant " +
            "LEFT JOIN Profile profile ON profile.id = competitionParticipant.user.profileId " +
            "WHERE competitionParticipant.competition.id = :competitionId " +
            "AND competitionParticipant.role = 'ASSESSOR' " +
            "AND (:status IS NULL OR competitionParticipant.status = :status) " +
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

    static final String PARTICIPANTS_WITHOUT_ASSESSMENTS = "SELECT cp FROM CompetitionParticipant cp WHERE cp.competition.id = :compId " +
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

    static final String PARTICIPANTS_WITH_ASSESSMENTS = "SELECT cp FROM CompetitionParticipant cp WHERE " +
            "cp.competition.id = :compId " +
            "AND cp.role = :role " +
            "AND cp.status = :status " +
            "AND EXISTS (SELECT 'found' FROM Assessment a WHERE a.participant.user = cp.user AND a.target.id = :appId)";

    @Override
    List<CompetitionParticipant> findAll();

    CompetitionParticipant getByInviteHash(String hash);

    List<CompetitionParticipant> getByUserIdAndRole(Long userId, CompetitionParticipantRole role);

    List<CompetitionParticipant> getByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    @Query(BY_COMP_AND_STATUS)
    Page<CompetitionParticipant> getAssessorsByCompetitionAndStatus(@Param("competitionId") long competitionId,
                                                                    @Param("status") ParticipantStatus status,
                                                                    Pageable pageable);

    @Query(BY_COMP_INNOVATION_AREA_STATUS_AND_COMPLIANT)
    Page<CompetitionParticipant> getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant(@Param("competitionId") long competitionId,
                                                                                                 @Param("innovationAreaId") Long innovationAreaId,
                                                                                                 @Param("status") ParticipantStatus status,
                                                                                                 @Param("isCompliant") Boolean isCompliant,
                                                                                                 Pageable pageable);

    List<CompetitionParticipant> getByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    List<CompetitionParticipant> getByInviteEmail(String email);

    int countByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    int countByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    @Query(PARTICIPANTS_WITHOUT_ASSESSMENTS)
    Page<CompetitionParticipant> findParticipantsWithoutAssessments(@Param("compId") long competitionId,
                                                                    @Param("role") CompetitionParticipantRole role,
                                                                    @Param("status") ParticipantStatus status,
                                                                    @Param("appId") long applicationId,
                                                                    @Param("innovationAreaId") Long filterInnovationArea,
                                                                    Pageable pageable);

    @Query(PARTICIPANTS_WITH_ASSESSMENTS)
    List<CompetitionParticipant> findParticipantsWithAssessments(
            @Param("compId") Long competitionId,
            @Param("role") CompetitionParticipantRole role,
            @Param("status") ParticipantStatus status,
            @Param("appId") Long applicationId);
}
