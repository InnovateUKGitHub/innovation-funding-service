package org.innovateuk.ifs.application.resource;

import java.util.List;

/**
 * A single slice of a paginated list of {@link ApplicationCountSummaryResource}s.
 */
public class AssessorCountSummaryPageResource extends AssessmentCountSummaryPageResource<AssessorCountSummaryResource> {
    public AssessorCountSummaryPageResource() {
        super();
    }

    public AssessorCountSummaryPageResource(long totalElements, int totalPages, List<AssessorCountSummaryResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}