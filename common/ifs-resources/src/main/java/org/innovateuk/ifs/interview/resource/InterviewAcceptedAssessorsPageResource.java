package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class InterviewAcceptedAssessorsPageResource extends PageResource<InterviewAcceptedAssessorsResource> {

    public InterviewAcceptedAssessorsPageResource() {
    }

    public InterviewAcceptedAssessorsPageResource(long totalElements, int totalPages, List<InterviewAcceptedAssessorsResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
