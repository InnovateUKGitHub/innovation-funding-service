package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationStatisticsRepository extends PagingAndSortingRepository<ApplicationStatistics, Long> {

    Sort SORT_BY_FIRSTNAME = new Sort("user.firstName");

    String APPLICATION_FILTER = "SELECT a FROM ApplicationStatistics a WHERE a.competition = :compId " +
            "AND (a.applicationProcess.activityState IN :states) " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%'))";

    String INNOVATION_AREA_FILTER = "SELECT a FROM ApplicationStatistics a " +
            "LEFT JOIN ApplicationInnovationAreaLink innovationArea ON innovationArea.application.id = a.id " +
            "AND (innovationArea.className = 'org.innovateuk.ifs.application.domain.Application#innovationArea') " +
            "WHERE a.competition = :compId " +
            "AND (a.applicationProcess.activityState IN :states) " +
            "AND (innovationArea.category.id = :innovationArea OR :innovationArea IS NULL) " +
            "AND NOT EXISTS (SELECT 'found' FROM Assessment b WHERE b.participant.user.id = :assessorId AND b.target.id = a.id) " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%'))";

    String SUBMITTED_STATES_STRING = "(org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED)";

    String REJECTED_AND_SUBMITTED_STATES_STRING =
            "(org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.WITHDRAWN," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED)";

    String NOT_ACCEPTED_OR_SUBMITTED_STATES_STRING =
            "(org.innovateuk.ifs.assessment.resource.AssessmentState.PENDING," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.WITHDRAWN," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.CREATED," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED)";


    List<ApplicationStatistics> findByCompetitionAndApplicationProcessActivityStateIn(long competitionId, Collection<ApplicationState> applicationStates);

    @Query(APPLICATION_FILTER)
    Page<ApplicationStatistics> findByCompetitionAndApplicationProcessActivityStateIn(@Param("compId") long competitionId,
                                                                                      @Param("states") Collection<ApplicationState> applicationStates,
                                                                                      @Param("filter") String filter,
                                                                                      Pageable pageable);

    @Query(INNOVATION_AREA_FILTER)
    Page<ApplicationStatistics> findByCompetitionAndInnovationAreaProcessActivityStateIn(@Param("compId") long competitionId,
                                                                                         @Param("assessorId") long assessorId,
                                                                                         @Param("states") Collection<ApplicationState> applicationStates,
                                                                                         @Param("filter") String filter,
                                                                                         @Param("innovationArea") Long innovationArea,
                                                                                         Pageable pageable);

    @Query("SELECT NEW org.innovateuk.ifs.application.resource.AssessorCountSummaryResource(" +
            "  user.id, " +
            "  concat(user.firstName, ' ', user.lastName), " +
            "  profile.skillsAreas, " +
            "  sum(case when application.id IS NOT NULL AND assessment.activityState NOT IN " + REJECTED_AND_SUBMITTED_STATES_STRING + " THEN 1 ELSE 0 END), " + // total assigned
            "  sum(case when application.id IS NOT NULL AND application.competition.id = :compId AND assessment.activityState NOT IN " + REJECTED_AND_SUBMITTED_STATES_STRING + " THEN 1 ELSE 0 END), " + // assigned
            "  sum(case when application.id IS NOT NULL AND application.competition.id = :compId AND assessment.activityState NOT IN " + NOT_ACCEPTED_OR_SUBMITTED_STATES_STRING + " THEN 1 ELSE 0 END), " + // accepted
            "  sum(case when application.id IS NOT NULL AND application.competition.id = :compId AND assessment.activityState     IN " + SUBMITTED_STATES_STRING    + " THEN 1 ELSE 0 END)  " +  // submitted
            ") " +
            "FROM AssessmentParticipant assessmentParticipant " +
            "JOIN User user ON user.id = assessmentParticipant.user.id " +
            "JOIN user.roleProfileStatuses roleStatuses " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            // join on all applications for each invited assessor on the system
            "LEFT JOIN ProcessRole processRole ON processRole.user.id = user.id AND processRole.role = org.innovateuk.ifs.user.resource.Role.ASSESSOR " +
            "LEFT JOIN Assessment assessment ON assessment.participant = processRole.id AND type(assessment) = Assessment " +
            "LEFT JOIN Application application ON assessment.target.id = application.id  " +
            "WHERE " +
            "  assessmentParticipant.competition.id = :compId AND " +
            "  assessmentParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED AND " +
            "  assessmentParticipant.role = 'ASSESSOR' AND " +
            "  roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR AND " +
            "  roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE AND " +
            "CONCAT(user.firstName, ' ', user.lastName) LIKE CONCAT('%', :assessorNameFilter, '%')" +
            "GROUP BY user ")
    Page<AssessorCountSummaryResource> getAssessorCountSummaryByCompetitionAndAssessorNameLike(@Param("compId") long competitionId,
                                                                            @Param("assessorNameFilter") String assessorNameFilter,
                                                                            Pageable pageable);
}