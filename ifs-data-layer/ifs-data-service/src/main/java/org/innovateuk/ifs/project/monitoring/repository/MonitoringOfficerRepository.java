package org.innovateuk.ifs.project.monitoring.repository;

import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface MonitoringOfficerRepository extends PagingAndSortingRepository<MonitoringOfficer, Long> {

    String PROJECT_STATES =
            "(org.innovateuk.ifs.project.resource.ProjectState.SETUP," +
                    "org.innovateuk.ifs.project.resource.ProjectState.ON_HOLD," +
                    "org.innovateuk.ifs.project.resource.ProjectState.LIVE)";

    String GET_UNASSIGNED = "SELECT NEW org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource(" +
            "   project.id," +
            "   project.application.id," +
            "   project.name" +
            ") " +
            "FROM Project project " +
            "LEFT JOIN MonitoringOfficer monitoringOfficer " +
            "   ON monitoringOfficer.project.id = project.id " +
            "   AND monitoringOfficer.role = org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.MONITORING_OFFICER " +
            "WHERE " +
            "   monitoringOfficer.id IS NULL " +
            "   AND project.projectProcess.activityState in " + PROJECT_STATES;

    String GET_ASSIGNED = "SELECT NEW org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource(" +
            "   project.id," +
            "   project.application.id," +
            "   project.application.competition.id," +
            "   project.name," +
            "   organisation.name" +
            ") " +
            "FROM Project project " +
            "JOIN ProcessRole processRole " +
            "   ON processRole.applicationId = project.application.id " +
            "   AND processRole.role = org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT " +
            "JOIN Organisation organisation " +
            "   ON organisation.id = processRole.organisationId " +
            "JOIN MonitoringOfficer monitoringOfficer " +
            "   ON monitoringOfficer.project.id = project.id " +
            "   AND monitoringOfficer.role = org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.MONITORING_OFFICER " +
            "WHERE " +
            "   monitoringOfficer.user.id = :userId " +
            "   AND project.projectProcess.activityState in " + PROJECT_STATES;

    String NOT_KTP = "AND project.application.competition.fundingType NOT IN (org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP)";
    String IS_KTP = "AND project.application.competition.fundingType = org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP ";

    List<MonitoringOfficer> findByUserId(long userId);

    Optional<MonitoringOfficer> findOneByProjectIdAndRole(long projectId, ProjectParticipantRole role);

    boolean existsByProjectIdAndUserId(long projectId, long userId);

    boolean existsByUserId(long userId);

    boolean existsByProjectApplicationIdAndUserId(long applicationId, long userId);

    boolean existsByProjectApplicationCompetitionIdAndUserId(long competitionId, long userId);

    void deleteByUserIdAndProjectId(long userId, long projectId);

    @Query(GET_UNASSIGNED +
            NOT_KTP +
            "ORDER BY project.application.id")
    List<MonitoringOfficerUnassignedProjectResource> findUnassignedProject();

    @Query(GET_UNASSIGNED +
            IS_KTP +
            "ORDER BY project.application.id")
    List<MonitoringOfficerUnassignedProjectResource> findUnassignedKTPProject();

    @Query(GET_ASSIGNED +
            NOT_KTP +
            "ORDER BY project.application.id")
    List<MonitoringOfficerAssignedProjectResource> findAssignedProjects(Long userId);

    @Query(GET_ASSIGNED +
            IS_KTP +
            "ORDER BY project.application.id")
    List<MonitoringOfficerAssignedProjectResource> findAssignedKTPProjects(Long userId);
}