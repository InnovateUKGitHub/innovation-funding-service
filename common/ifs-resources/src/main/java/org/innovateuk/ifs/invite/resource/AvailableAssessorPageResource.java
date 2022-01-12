package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link AvailableAssessorResource}s.
 */
public class AvailableAssessorPageResource extends PageResource<AvailableAssessorResource> {

    public AvailableAssessorPageResource() {
    }

    public AvailableAssessorPageResource(long totalElements,
                                         int totalPages,
                                         List<AvailableAssessorResource> content,
                                         int number,
                                         int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
