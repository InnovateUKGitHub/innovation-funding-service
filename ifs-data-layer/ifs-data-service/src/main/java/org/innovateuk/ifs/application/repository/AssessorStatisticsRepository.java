package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.AssessorStatistics;
import org.innovateuk.ifs.application.domain.AssessorStatisticsResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessorStatisticsRepository extends PagingAndSortingRepository<AssessorStatistics, Long> {

    String APPLICATION_FILTER =
            "SELECT a FROM AssessorStatistics a " +
//                    "INNER JOIN Assessment assessment " +
//                    "INNER JOIN Assessment assessment ON (assessment.participant IN a.processRoles) " +
            "WHERE a.competitionParticipant.competition.id = :compId AND a.competitionParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED ";// +
//            "AND (assessment.activityState.state IN :states) " +
//            "AND (str(assessment.id) LIKE CONCAT('%', :filter, '%'))";

//    List<AssessorStatistics> findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> applicationStates);
    @Query("SELECT a FROM AssessorStatistics a WHERE a.competitionParticipant.competition.id = :compId")
    List<AssessorStatistics> findByCompetitionParticipantCompetitionId(@Param("compId") long competitionId);

    @Query(APPLICATION_FILTER)
    Page<AssessorStatistics> findByCompetitionParticipantCompetitionIdAndApplicationProcessActivityStateStateIn(@Param("compId") long competitionId,
                                                                                                                Pageable pageable);

    Set<AssessmentStates> ASSESSOR_STATES = EnumSet.complementOf(EnumSet.of(REJECTED, WITHDRAWN)); // want the backing states

    Set<AssessmentStates> ACCEPTED_STATES = EnumSet.complementOf(EnumSet.of(PENDING, REJECTED, WITHDRAWN, CREATED));


//    String ASSESOR_STATES_STRING = String.join(",", ASSESSOR_STATES.stream().map(e -> e.getClass().getName() + "." + e.name() ).collect(toSet()));

    // TODO pass in the states as enum sets from the service, rather than hardcoding strings
    String REJECTED_STATES_STRING = "(org.innovateuk.ifs.workflow.resource.State.REJECTED,org.innovateuk.ifs.workflow.resource.State.WITHDRAWN)";
    String NOT_ACCEPTED_STATES_STRING = "(org.innovateuk.ifs.workflow.resource.State.PENDING,org.innovateuk.ifs.workflow.resource.State.REJECTED," +
            "org.innovateuk.ifs.workflow.resource.State.WITHDRAWN,org.innovateuk.ifs.workflow.resource.State.CREATED)";
    String SUBMITTED_STATES_STRING = "(org.innovateuk.ifs.workflow.resource.State.SUBMITTED)";

    @Query("SELECT NEW org.innovateuk.ifs.application.resource.AssessorCountSummaryResource(" +
            "  user.id, " +
            "  concat(user.firstName, ' ', user.lastName), " +
            "  profile.skillsAreas, " +
            "  sum(case when activityState.state NOT IN " + REJECTED_STATES_STRING     + " THEN 1 ELSE 0 END), " + // total assigned
            "  sum(case when competitionParticipant.competition.id = :compId AND activityState.state NOT IN " + REJECTED_STATES_STRING     + " THEN 1 ELSE 0 END), " + // assigned
            "  sum(case when competitionParticipant.competition.id = :compId AND activityState.state NOT IN " + NOT_ACCEPTED_STATES_STRING + " THEN 1 ELSE 0 END), " + // accepted
            "  sum(case when competitionParticipant.competition.id = :compId AND activityState.state     IN " + SUBMITTED_STATES_STRING    + " THEN 1 ELSE 0 END)  " +  // submitted
            ") " +
            "FROM User user " +
            "JOIN CompetitionParticipant competitionParticipant ON competitionParticipant.user = user " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            "LEFT JOIN Application application ON application.competition = competitionParticipant.competition  " + // AND application.applicationProcess.activityState.state IN :submittedStates " +
            "LEFT JOIN ProcessRole processRole ON processRole.user = user AND processRole.applicationId = application.id " +
            "LEFT JOIN Assessment assessment ON assessment.participant = processRole.id AND assessment.target = application " +
            "LEFT JOIN ActivityState activityState ON assessment.activityState = activityState.id " +
            "WHERE " +
            "  (str(user.id) LIKE CONCAT('%', :filter, '%') OR 1=1) AND " +
            "  competitionParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED " +
            "GROUP BY user, profile " +
            "HAVING sum(case when competitionParticipant.competition.id = :compId THEN 1 ELSE 0 END) > 0")
    Page<AssessorCountSummaryResource> getAssessorCountSummaryByCompetition(@Param("compId") long competitionId,
//                                                                            @Param("submittedStates") Collection<State> submittedStates, // do we need to filter ineligible?
                                                                            @Param("filter") String filter,
                                                                            Pageable pageable);
}