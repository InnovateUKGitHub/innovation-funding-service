package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

/**
 * Resource encapsulating a pageable list of {@link AvailableAssessorResource}s.
 */
public class AvailableAssessorPageResource extends PageResource<AvailableAssessorResource> {

    public enum Order {
        FIRST_NAME("first_name");

        private String column;

        Order(String column) {
            this.column = column;
        }

        public String getColumn() {
            return column;
        }
    }

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
