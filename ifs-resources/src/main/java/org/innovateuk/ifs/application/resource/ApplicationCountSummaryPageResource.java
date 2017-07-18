package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * A single slice of a paginated list of {@link ApplicationCountSummaryResource}s.
 */
public class ApplicationCountSummaryPageResource extends AssessmentCountSummaryPageResource<ApplicationCountSummaryResource> {
    public ApplicationCountSummaryPageResource() {
        super();
    }

    public ApplicationCountSummaryPageResource(long totalElements, int totalPages, List<ApplicationCountSummaryResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
