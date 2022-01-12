package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link AssessorInviteOverviewResource}s.
 */
public class AssessorInviteOverviewPageResource extends PageResource<AssessorInviteOverviewResource> {

    public AssessorInviteOverviewPageResource() {
    }

    public AssessorInviteOverviewPageResource(long totalElements, int totalPages, List<AssessorInviteOverviewResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
