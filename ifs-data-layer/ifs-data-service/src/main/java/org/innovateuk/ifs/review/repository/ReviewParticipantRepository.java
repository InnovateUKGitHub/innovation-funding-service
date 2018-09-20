package org.innovateuk.ifs.review.repository;

import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
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
public interface ReviewParticipantRepository extends PagingAndSortingRepository<ReviewParticipant, Long> {

    String USERS_WITH_ASSESSMENT_PANEL_INVITE = "SELECT invite.user.id " +
            "FROM ReviewInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    String BY_COMP_AND_STATUS_ON_PANEL = "SELECT assessmentPanelParticipant " +
            "FROM ReviewParticipant assessmentPanelParticipant " +
            "WHERE assessmentPanelParticipant.competition.id = :competitionId " +
            "AND assessmentPanelParticipant.role = 'PANEL_ASSESSOR' " +
            "AND assessmentPanelParticipant.status IN :status " +
            "AND assessmentPanelParticipant.user.id IN (" + USERS_WITH_ASSESSMENT_PANEL_INVITE + ")";

    @Query(BY_COMP_AND_STATUS_ON_PANEL)
    Page<ReviewParticipant> getPanelAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                            @Param("status") List<ParticipantStatus> status,
                                                                            Pageable pageable);

    @Query(BY_COMP_AND_STATUS_ON_PANEL)
    List<ReviewParticipant> getPanelAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                            @Param("status") List<ParticipantStatus> status);

    @Override
    List<ReviewParticipant> findAll();

    ReviewParticipant getByInviteHash(String hash);

    List<ReviewParticipant> findByUserIdAndRole(long userId, CompetitionParticipantRole role);

    int countByCompetitionIdAndRoleAndStatusAndInviteIdIn(long competitionId,
                                                          CompetitionParticipantRole role,
                                                          ParticipantStatus status,
                                                          List<Long> inviteIds);
}