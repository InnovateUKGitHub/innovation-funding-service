package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link InterviewPanelCreatedInviteResource}s.
 */
public class InterviewPanelCreatedInvitePageResource extends PageResource<InterviewPanelCreatedInviteResource> {

    public InterviewPanelCreatedInvitePageResource() {
    }

    public InterviewPanelCreatedInvitePageResource(long totalElements, int totalPages, List<InterviewPanelCreatedInviteResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
