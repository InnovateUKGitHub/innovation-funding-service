package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.resource.PageResource;


import java.util.List;

public class EuContactPageResource extends PageResource<EuContactResource> {

        public EuContactPageResource() {

        }

        public EuContactPageResource(long totalElements,
                                     int totalPages,
                                     List<EuContactResource> content,
                                     int number,
                                     int size) {
            super(totalElements, totalPages, content, number, size);
        }
}
