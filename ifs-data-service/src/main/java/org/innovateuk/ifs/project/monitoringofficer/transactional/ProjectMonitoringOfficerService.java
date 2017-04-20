package org.innovateuk.ifs.project.monitoringofficer.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.transactional.SaveMonitoringOfficerResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Project processing work
 */
public interface ProjectMonitoringOfficerService {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_MONITORING_OFFICER')")
    ServiceResult<MonitoringOfficerResource> getMonitoringOfficer(Long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'ASSIGN_MONITORING_OFFICER')")
    ServiceResult<SaveMonitoringOfficerResult> saveMonitoringOfficer(Long projectId, MonitoringOfficerResource monitoringOfficerResource);

    @PreAuthorize("hasPermission(#monitoringOfficer.project, 'org.innovateuk.ifs.project.resource.ProjectResource', 'ASSIGN_MONITORING_OFFICER')")
    ServiceResult<Void> notifyStakeholdersOfMonitoringOfficerChange(MonitoringOfficerResource monitoringOfficer);
}