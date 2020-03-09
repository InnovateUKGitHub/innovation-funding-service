package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * DTO for an assessor that is either allocatable, or previously allocated to an application.
 */
public class ApplicationAvailableAssessorPageResource extends PageResource<ApplicationAvailableAssessorResource> {
    public ApplicationAvailableAssessorPageResource() {
        super();
    }

    public ApplicationAvailableAssessorPageResource(long totalElements, int totalPages, List<ApplicationAvailableAssessorResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}