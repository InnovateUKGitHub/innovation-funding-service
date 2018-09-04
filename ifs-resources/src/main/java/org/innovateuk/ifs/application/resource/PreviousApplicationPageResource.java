package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource for paging Applications
 */
public class PreviousApplicationPageResource extends PageResource<PreviousApplicationResource> {
    public PreviousApplicationPageResource() {
        super();
    }

    public PreviousApplicationPageResource(long totalElements, int totalPages, List<PreviousApplicationResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
