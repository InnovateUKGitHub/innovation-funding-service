package org.innovateuk.ifs.project.monitoring.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerDashboardPageResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.SimpleUserResource;

import java.util.List;

public interface MonitoringOfficerRestService {

    RestResult<MonitoringOfficerAssignmentResource> getProjectMonitoringOfficer(long projectMonitoringOfficerId);

    RestResult<Void> assignMonitoringOfficerToProject(long projectMonitoringOfficerId, long projectId);

    RestResult<Void> unassignMonitoringOfficerFromProject(long monitoringOfficerId, long projectId);

    RestResult<List<SimpleUserResource>> findAll();

    RestResult<List<SimpleUserResource>> findAllKtp();

    RestResult<List<SimpleUserResource>> findAllNonKtp();

    RestResult<List<ProjectResource>> getProjectsForMonitoringOfficer(long userId);

    RestResult<MonitoringOfficerResource> findMonitoringOfficerForProject(long projectId);

    RestResult<Boolean> isMonitoringOfficerOnProject(long projectId, long userId);

    RestResult<Boolean> isMonitoringOfficer(long userId);

    RestResult<Void> sendDocumentReviewNotification(long projectId, long userId);

    RestResult<MonitoringOfficerDashboardPageResource> filterProjectsForMonitoringOfficer(long projectMonitoringOfficerId,
                                                                                          int pageNumber,
                                                                                          int pageSize,
                                                                                          String keywordSearch,
                                                                                          boolean projectInSetup,
                                                                                          boolean previousProject);

    RestResult<List<ProjectResource>> filterProjectsForMonitoringOfficer(long projectMonitoringOfficerId,
                                                                                          String keywordSearch,
                                                                                          boolean projectInSetup,
                                                                                          boolean previousProject);
}