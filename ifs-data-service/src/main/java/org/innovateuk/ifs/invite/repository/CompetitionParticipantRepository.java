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

    @Override
    List<CompetitionParticipant> findAll();

    CompetitionParticipant getByInviteHash(String hash);

    List<CompetitionParticipant> getByUserIdAndRole(Long userId, CompetitionParticipantRole role);

    List<CompetitionParticipant> getByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    @Query("SELECT competitionParticipant " +
            "FROM CompetitionParticipant competitionParticipant " +
            "JOIN Profile profile ON profile.id = competitionParticipant.user.profileId " +
            "JOIN profile.innovationAreas innovationAreas " +
            "WHERE competitionParticipant.competition.id = :competitionId " +
            "AND competitionParticipant.role = 'ASSESSOR' " +
            "AND (:statusId IS NULL OR competitionParticipant.status.id = :statusId) " +
            "AND (:innovationAreaId IS NULL OR innovationAreas.category.id = :innovationAreaId) " +
            "AND (:isCompliant IS NULL OR (:isCompliant = true AND (" +
            "   EXISTS(" +
            "       SELECT affiliation.id " +
            "       FROM Affiliation affiliation " +
            "       WHERE affiliation.user.id = competitionParticipant.user.id " +
            "   ) " +
            "   AND profile.skillsAreas IS NOT NULL " +
            "   AND profile.contract IS NOT NULL " +
            ")))")
    Page<CompetitionParticipant> getAssessorsByCompetitionAndInnovationAreaAndStatusAndContract(@Param("competitionId") long competitionId,
                                                                                                @Param("innovationAreaId") Long innovationAreaId,
                                                                                                @Param("statusId") Long statusId,
                                                                                                @Param("isCompliant") Boolean isCompliant,
                                                                                                Pageable pageable);

    List<CompetitionParticipant> getByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);

    List<CompetitionParticipant> getByInviteEmail(String email);

    int countByCompetitionIdAndRole(Long competitionId, CompetitionParticipantRole role);

    int countByCompetitionIdAndRoleAndStatus(Long competitionId, CompetitionParticipantRole role, ParticipantStatus status);
}
