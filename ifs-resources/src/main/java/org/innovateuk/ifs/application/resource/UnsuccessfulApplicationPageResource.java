package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource for paging Applications
 */
public class UnsuccessfulApplicationPageResource extends PageResource<UnsuccessfulApplicationResource> {
    public UnsuccessfulApplicationPageResource() {
        super();
    }

    public UnsuccessfulApplicationPageResource(long totalElements, int totalPages, List<UnsuccessfulApplicationResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
