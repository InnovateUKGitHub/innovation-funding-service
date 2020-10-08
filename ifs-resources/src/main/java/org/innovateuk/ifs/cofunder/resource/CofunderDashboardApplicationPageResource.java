package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class CofunderDashboardApplicationPageResource extends PageResource<CofunderDashboardApplicationResource> {
    public CofunderDashboardApplicationPageResource() {
    }

    public CofunderDashboardApplicationPageResource(long totalElements, int totalPages, List<CofunderDashboardApplicationResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
