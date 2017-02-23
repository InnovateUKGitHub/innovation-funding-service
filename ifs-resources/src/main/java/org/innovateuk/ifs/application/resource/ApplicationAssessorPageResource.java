package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class ApplicationAssessorPageResource extends PageResource<ApplicationAssessorResource> {
    public ApplicationAssessorPageResource() {
        super();
    }

    public ApplicationAssessorPageResource(long totalElements, int totalPages, List<ApplicationAssessorResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);

    }
}
