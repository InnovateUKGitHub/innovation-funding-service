package org.innovateuk.ifs.project.status.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class ProjectStatusPageResource extends PageResource<ProjectStatusResource> {
    public ProjectStatusPageResource() {
    }

    public ProjectStatusPageResource(long totalElements, int totalPages, List<ProjectStatusResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
