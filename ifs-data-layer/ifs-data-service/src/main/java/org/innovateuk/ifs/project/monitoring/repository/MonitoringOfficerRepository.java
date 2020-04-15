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


    List<MonitoringOfficer> findByUserId(long userId);

    Optional<MonitoringOfficer> findOneByProjectIdAndRole(long projectId, ProjectParticipantRole role);

    boolean existsByProjectIdAndUserId(long projectId, long userId);

    boolean existsByProjectApplicationIdAndUserId(long applicationId, long userId);

    boolean existsByProjectApplicationCompetitionIdAndUserId(long competitionId, long userId);

    void deleteByUserIdAndProjectId(long userId, long projectId);

    @Query("SELECT NEW org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource(" +
            "   project.id," +
            "   project.application.id," +
            "   project.name" +
            ") " +
            "FROM Project project " +
            "LEFT JOIN MonitoringOfficer monitoringOfficer " +
            "   ON monitoringOfficer.id = project.projectMonitoringOfficer.id " +
            "WHERE " +
            "   monitoringOfficer.id IS NULL " +
            "   AND project.projectProcess.activityState in " + PROJECT_STATES +
            "ORDER BY project.application.id")
    List<MonitoringOfficerUnassignedProjectResource> findUnassignedProject();

    @Query("SELECT NEW org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource(" +
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
            "WHERE " +
            "   project.projectMonitoringOfficer.user.id = :userId " +
            "   AND project.projectProcess.activityState in " + PROJECT_STATES +
            "ORDER BY project.application.id")
    List<MonitoringOfficerAssignedProjectResource> findAssignedProjects(Long userId);
}