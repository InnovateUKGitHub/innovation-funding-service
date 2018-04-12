package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link InterviewAssignmentApplicationResource}s.
 */
public class InterviewAssignmentApplicationPageResource extends PageResource<InterviewAssignmentApplicationResource> {

    public InterviewAssignmentApplicationPageResource() {
    }

    public InterviewAssignmentApplicationPageResource(long totalElements,
                                                      int totalPages,
                                                      List<InterviewAssignmentApplicationResource> content,
                                                      int number,
                                                      int size) {
        super(totalElements, totalPages, content, number, size);
    }
}