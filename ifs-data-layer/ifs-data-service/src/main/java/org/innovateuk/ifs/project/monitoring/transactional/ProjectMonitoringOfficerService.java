package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectMonitoringOfficerService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(long userId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> assignProjectToMonitoringOfficer(long userId, long projectId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> unassignProjectFromMonitoringOfficer(long userId, long projectId);
}