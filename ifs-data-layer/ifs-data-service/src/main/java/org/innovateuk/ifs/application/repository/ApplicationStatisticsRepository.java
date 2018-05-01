package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationStatisticsRepository extends PagingAndSortingRepository<ApplicationStatistics, Long> {

    Sort SORT_BY_FIRSTNAME = new Sort("firstName");

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
            "  sum(case when application.id IS NOT NULL AND assessmentParticipant.competition.id = :compId AND assessment.activityState NOT IN " + REJECTED_AND_SUBMITTED_STATES_STRING + " THEN 1 ELSE 0 END), " + // assigned
            "  sum(case when application.id IS NOT NULL AND assessmentParticipant.competition.id = :compId AND assessment.activityState NOT IN " + NOT_ACCEPTED_OR_SUBMITTED_STATES_STRING + " THEN 1 ELSE 0 END), " + // accepted
            "  sum(case when application.id IS NOT NULL AND assessmentParticipant.competition.id = :compId AND assessment.activityState     IN " + SUBMITTED_STATES_STRING    + " THEN 1 ELSE 0 END)  " +  // submitted
            ") " +
            "FROM User user " +
            "JOIN AssessmentParticipant assessmentParticipant ON assessmentParticipant.user.id = user.id " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            // join on all applications for each invited assessor on the system
            "LEFT JOIN ProcessRole processRole ON processRole.user.id = user.id " +
            "LEFT JOIN Assessment assessment ON assessment.participant = processRole.id " +
            "LEFT JOIN Application application ON assessment.target.id = application.id AND application.competition.id = assessmentParticipant.competition.id  " +
            "WHERE " +
            "  assessmentParticipant.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED AND " +
            "  assessmentParticipant.role = 'ASSESSOR' AND " +
            " (:innovationSectorId IS NULL OR :innovationSectorId IN (SELECT innovationAreaLink.category.sector.id " +
            "                                             FROM ProfileInnovationAreaLink innovationAreaLink" +
            "                                             WHERE innovationAreaLink.profile = profile)) AND " +
            "  (:businessType IS NULL OR profile.businessType = :businessType) " +
            "GROUP BY user " +
            "HAVING sum(case when assessmentParticipant.competition.id = :compId THEN 1 ELSE 0 END) > 0")
    Page<AssessorCountSummaryResource> getAssessorCountSummaryByCompetition(@Param("compId") long competitionId,
                                                                            @Param("innovationSectorId") Optional<Long> innovationSectorId,
                                                                            @Param("businessType") Optional<BusinessType> businessType,
                                                                            Pageable pageable);
}