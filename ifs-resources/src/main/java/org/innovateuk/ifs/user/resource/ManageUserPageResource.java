package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class ManageUserPageResource extends PageResource<ManageUserResource> {
    public ManageUserPageResource() {
        super();
    }

    public ManageUserPageResource(long totalElements, int totalPages, List<ManageUserResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}