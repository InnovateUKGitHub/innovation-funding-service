package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * A single slice of a paginated list of {@link AssessorCountSummaryResource}s.
 */
public abstract class AssessmentCountSummaryPageResource<T extends AssessmentCountSummaryResource> extends PageResource<T> {
    protected AssessmentCountSummaryPageResource() {
        super();
    }

    protected AssessmentCountSummaryPageResource(long totalElements, int totalPages, List<T> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
