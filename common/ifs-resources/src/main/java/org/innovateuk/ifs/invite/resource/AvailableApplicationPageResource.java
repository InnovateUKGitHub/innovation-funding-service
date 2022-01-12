package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link AvailableApplicationResource}s.
 */
public class AvailableApplicationPageResource extends PageResource<AvailableApplicationResource> {

    public AvailableApplicationPageResource() {
    }

    public AvailableApplicationPageResource(long totalElements,
                                            int totalPages,
                                            List<AvailableApplicationResource> content,
                                            int number,
                                            int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
