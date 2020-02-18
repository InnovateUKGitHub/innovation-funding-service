package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
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
    String SUBMITTED_STATES_STRING = "(org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED)";

    String REJECTED_STATES_STRING =
            "(org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.WITHDRAWN)";

    String NOT_ACCEPTED_STATES =
            "(org.innovateuk.ifs.assessment.resource.AssessmentState.CREATED," +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.PENDING, " +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED, " +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.WITHDRAWN, " +
                    "org.innovateuk.ifs.assessment.resource.AssessmentState.CREATED)";

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

    String SUBMITTED_APPLICATION_STATES = "(org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED," +
            "org.innovateuk.ifs.application.resource.ApplicationState.APPROVED," +
            "org.innovateuk.ifs.application.resource.ApplicationState.REJECTED)";

    Sort SORT_BY_FIRSTNAME = new Sort("user.firstName");

    String APPLICATION_FILTER = "SELECT a FROM ApplicationStatistics a WHERE a.competition = :compId " +
            "AND (a.applicationProcess.activityState IN :states) " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%'))";

    String SUM_ASSESSORS = "SUM(CASE WHEN assessment.id IS NOT NULL AND assessment.activityState NOT IN " + REJECTED_STATES_STRING + " THEN 1 ELSE 0 END)";
    String SUM_ACCEPTED = "SUM(CASE WHEN assessment.id IS NOT NULL AND assessment.activityState NOT IN " + NOT_ACCEPTED_STATES + " THEN 1 ELSE 0 END)";
    String SUM_SUBMITTED = "SUM(CASE WHEN assessment.id IS NOT NULL AND assessment.activityState IN " + SUBMITTED_STATES_STRING + " THEN 1 ELSE 0 END)";

    String ASSESSOR_FILTER =
            " FROM Application application " +
            " JOIN ProcessRole leadRole ON leadRole.applicationId = application.id AND leadRole.role = org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT " +
            " JOIN Organisation lead ON lead.id = leadRole.organisationId " +
            " LEFT JOIN Assessment assessment ON assessment.target.id = application.id AND type(assessment) = Assessment " +
            "WHERE application.competition.id = :competitionId " +
            "AND (application.applicationProcess.activityState IN " + SUBMITTED_APPLICATION_STATES + ") " +
            "AND NOT EXISTS (SELECT 'found' FROM Assessment b WHERE b.participant.user.id = :assessorId AND b.target.id = application.id) " +
            "AND (str(application.id) LIKE CONCAT('%', :filter, '%')) " +
            "GROUP BY application.id";

    List<ApplicationStatistics> findByCompetitionAndApplicationProcessActivityStateIn(long competitionId, Collection<ApplicationState> applicationStates);

    @Query(APPLICATION_FILTER)
    Page<ApplicationStatistics> findByCompetitionAndApplicationProcessActivityStateIn(@Param("compId") long competitionId,
                                                                                      @Param("states") Collection<ApplicationState> applicationStates,
                                                                                      @Param("filter") String filter,
                                                                                      Pageable pageable);

    @Query("SELECT NEW org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource(" +
            " application.id, " +
            " application.name, " +
            " lead.name, " +
            SUM_ASSESSORS + ", " +
            SUM_ACCEPTED + ", " +
            SUM_SUBMITTED +
            ")" +
            ASSESSOR_FILTER)
    Page<ApplicationCountSummaryResource> findStatisticsForApplicationsNotAssignedTo(long competitionId,
                                                                                     long assessorId,
                                                                                     String filter,
                                                                                     Pageable pageable);


    @Query("SELECT application.id " +
            ASSESSOR_FILTER)
    List<Long> findApplicationIdsNotAssignedTo(long competitionId,
                                               long assessorId,
                                               String filter);

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