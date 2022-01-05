package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource for paging Applications
 */
public class ApplicationPageResource extends PageResource<ApplicationResource> {
    public ApplicationPageResource() {
        super();
    }

    public ApplicationPageResource(long totalElements, int totalPages, List<ApplicationResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
