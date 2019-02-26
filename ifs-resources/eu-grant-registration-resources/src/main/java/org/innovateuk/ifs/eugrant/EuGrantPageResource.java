package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class EuGrantPageResource extends PageResource<EuGrantResource> {

    public EuGrantPageResource() {

    }

    public EuGrantPageResource(long totalElements,
                                 int totalPages,
                                 List<EuGrantResource> content,
                                 int number,
                                 int size) {
        super(totalElements, totalPages, content, number, size);
    }

}
