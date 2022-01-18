package org.innovateuk.ifs.project.monitoring.resource;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

public class MonitoringOfficerDashboardPageResource extends PageResource<ProjectResource> {
    public MonitoringOfficerDashboardPageResource() {
        super();
    }

    public MonitoringOfficerDashboardPageResource(long totalElements, int totalPages, List<ProjectResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}