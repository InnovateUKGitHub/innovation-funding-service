package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;

import java.util.List;

public class AssessorInterviewAllocationPageResource extends PageResource<AssessorInterviewAllocationResource> {

    public AssessorInterviewAllocationPageResource() {
    }

    public AssessorInterviewAllocationPageResource(long totalElements, int totalPages, List<AssessorInterviewAllocationResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
