package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link InterviewPanelStagedApplicationResource}s.
 */
public class InterviewPanelStagedApplicationPageResource extends PageResource<InterviewPanelStagedApplicationResource> {

    public InterviewPanelStagedApplicationPageResource() {
    }

    public InterviewPanelStagedApplicationPageResource(long totalElements,
                                                       int totalPages,
                                                       List<InterviewPanelStagedApplicationResource> content,
                                                       int number,
                                                       int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
