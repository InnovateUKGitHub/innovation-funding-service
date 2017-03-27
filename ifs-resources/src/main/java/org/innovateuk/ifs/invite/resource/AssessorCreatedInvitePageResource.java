package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link AssessorCreatedInviteResource}s.
 */
public class AssessorCreatedInvitePageResource extends PageResource<AssessorCreatedInviteResource> {

    public AssessorCreatedInvitePageResource() {
    }

    public AssessorCreatedInvitePageResource(long totalElements, int totalPages, List<AssessorCreatedInviteResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
