package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * A single slice of a paginated list of {@link ApplicationSummaryResource}s.
 */
public class ApplicationSummaryPageResource extends PageResource<ApplicationSummaryResource> {
    public ApplicationSummaryPageResource(long totalElements, int totalPages, List<ApplicationSummaryResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }

    public ApplicationSummaryPageResource(){
        super();
    }
}
