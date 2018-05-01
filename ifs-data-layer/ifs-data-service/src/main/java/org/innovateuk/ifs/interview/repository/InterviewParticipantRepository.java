package org.innovateuk.ifs.interview.repository;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
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
public interface InterviewParticipantRepository extends PagingAndSortingRepository<InterviewParticipant, Long> {

    String USERS_WITH_INTERVIEW_PANEL_INVITE = "SELECT invite.user.id " +
            "FROM InterviewInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    String BY_COMP_AND_STATUS_ON_PANEL = "SELECT assessmentInterviewPanelParticipant " +
            "FROM InterviewParticipant assessmentInterviewPanelParticipant " +
            "WHERE assessmentInterviewPanelParticipant.competition.id = :competitionId " +
            "AND assessmentInterviewPanelParticipant.role = org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR " +
            "AND assessmentInterviewPanelParticipant.status IN :status " +
            "AND assessmentInterviewPanelParticipant.user.id IN (" + USERS_WITH_INTERVIEW_PANEL_INVITE + ")";

    @Query(BY_COMP_AND_STATUS_ON_PANEL)
    Page<InterviewParticipant> getInterviewPanelAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                                        @Param("status") List<ParticipantStatus> status,
                                                                                        Pageable pageable);

    @Query("SELECT NEW org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationResource(" +
            "  user.id, " +
            "  concat(user.firstName, ' ', user.lastName), " +
            "  profile.skillsAreas " +
            ") " +
            "FROM InterviewParticipant interviewParticipant " +
            "JOIN User user ON interviewParticipant.user.id = user.id " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            "WHERE " +
            "  interviewParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED AND " +
            "  interviewParticipant.competition.id = :competitionId AND " +
            "  interviewParticipant.role = org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INTERVIEW_ASSESSOR AND" +
            "  interviewParticipant.user.id IN (" + USERS_WITH_INTERVIEW_PANEL_INVITE + ")"
    )
    Page<AssessorInterviewAllocationResource> getAllocateApplicationsOverview(@Param("competitionId") long competitionId, Pageable pageable);


    @Query(BY_COMP_AND_STATUS_ON_PANEL)
    List<InterviewParticipant> getInterviewPanelAssessorsByCompetitionAndStatusContains(@Param("competitionId") long competitionId,
                                                                                        @Param("status") List<ParticipantStatus> status);

    @Override
    List<InterviewParticipant> findAll();

    InterviewParticipant getByInviteHash(String hash);

    List<InterviewParticipant> findByUserIdAndRole(long userId, CompetitionParticipantRole role);

    int countByCompetitionIdAndRoleAndStatusAndInviteIdIn(long competitionId,
                                                          CompetitionParticipantRole role,
                                                          ParticipantStatus status,
                                                          List<Long> inviteIds);
}