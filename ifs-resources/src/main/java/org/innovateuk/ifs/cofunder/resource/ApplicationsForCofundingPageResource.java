package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class ApplicationsForCofundingPageResource extends PageResource<ApplicationsForCofundingResource> {
    public ApplicationsForCofundingPageResource() {
    }

    public ApplicationsForCofundingPageResource(long totalElements, int totalPages, List<ApplicationsForCofundingResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
