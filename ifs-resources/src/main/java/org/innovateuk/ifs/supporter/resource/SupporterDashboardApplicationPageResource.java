package org.innovateuk.ifs.supporter.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class SupporterDashboardApplicationPageResource extends PageResource<SupporterDashboardApplicationResource> {
    public SupporterDashboardApplicationPageResource() {
    }

    public SupporterDashboardApplicationPageResource(long totalElements, int totalPages, List<SupporterDashboardApplicationResource> content, int number, int size) {
        super(totalElements, totalPages, content, number, size);
    }
}
