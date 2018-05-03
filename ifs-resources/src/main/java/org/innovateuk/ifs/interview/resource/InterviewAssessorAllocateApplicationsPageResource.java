package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class InterviewAssessorAllocateApplicationsPageResource extends PageResource<InterviewAssessorAllocateApplicationsResource> {

    public InterviewAssessorAllocateApplicationsPageResource() {
    }

    public InterviewAssessorAllocateApplicationsPageResource(long totalElements, int totalPages, List<InterviewAssessorAllocateApplicationsResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
