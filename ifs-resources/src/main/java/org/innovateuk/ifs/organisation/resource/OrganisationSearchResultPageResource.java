package org.innovateuk.ifs.organisation.resource;

import org.innovateuk.ifs.commons.resource.PageResource;


import java.util.List;

public class OrganisationSearchResultPageResource extends PageResource<OrganisationSearchResult> {
    public OrganisationSearchResultPageResource() {
        super();
    }

    public OrganisationSearchResultPageResource(long totalElements, int totalPages, List<OrganisationSearchResult> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}