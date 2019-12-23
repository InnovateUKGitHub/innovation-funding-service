package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource for paging Role invites
 */
public class InvitePageResource extends PageResource<InviteResource> {
    public InvitePageResource() {
        super();
    }

    public InvitePageResource(long totalElements, int totalPages, List<InviteResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}