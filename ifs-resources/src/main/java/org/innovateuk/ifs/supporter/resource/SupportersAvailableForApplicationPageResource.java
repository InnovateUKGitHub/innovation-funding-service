package org.innovateuk.ifs.supporter.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class SupportersAvailableForApplicationPageResource extends PageResource<SupporterUserResource> {

    private List<SupporterUserResource> assignedSupporters;

    public SupportersAvailableForApplicationPageResource() {
    }

    public SupportersAvailableForApplicationPageResource(long totalElements, int totalPages, List<SupporterUserResource> content, int number, int size, List<SupporterUserResource> assignedSupporters) {
        super(totalElements, totalPages, content, number, size);
        this.assignedSupporters = assignedSupporters;
    }

    public List<SupporterUserResource> getAssignedSupporters() {
        return assignedSupporters;
    }

    public void setAssignedSupporters(List<SupporterUserResource> assignedSupporters) {
        this.assignedSupporters = assignedSupporters;
    }
}
