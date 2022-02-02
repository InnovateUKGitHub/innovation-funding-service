package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link InterviewAssignmentStagedApplicationResource}s.
 */
public class InterviewAssignmentStagedApplicationPageResource extends PageResource<InterviewAssignmentStagedApplicationResource> {

    public InterviewAssignmentStagedApplicationPageResource() {
    }

    public InterviewAssignmentStagedApplicationPageResource(long totalElements,
                                                            int totalPages,
                                                            List<InterviewAssignmentStagedApplicationResource> content,
                                                            int number,
                                                            int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
