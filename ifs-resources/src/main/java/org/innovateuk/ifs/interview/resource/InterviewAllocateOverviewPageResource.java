package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class InterviewAllocateOverviewPageResource extends PageResource<InterviewAllocateOverviewResource> {

    public InterviewAllocateOverviewPageResource() {
    }

    public InterviewAllocateOverviewPageResource(long totalElements, int totalPages, List<InterviewAllocateOverviewResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
