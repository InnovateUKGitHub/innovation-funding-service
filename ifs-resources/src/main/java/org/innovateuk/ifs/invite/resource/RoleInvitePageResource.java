package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource for paging Role invites
 */
public class RoleInvitePageResource extends PageResource<RoleInviteResource> {
    public RoleInvitePageResource() {
        super();
    }

    public RoleInvitePageResource(long totalElements, int totalPages, List<RoleInviteResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
