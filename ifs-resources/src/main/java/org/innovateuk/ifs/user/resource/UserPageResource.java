package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource for paging users
 */
public class UserPageResource extends PageResource<UserResource> {
    public UserPageResource() {
        super();
    }

    public UserPageResource(long totalElements, int totalPages, List<UserResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
