package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class CofundersAvailableForApplicationPageResource extends PageResource<CofunderUserResource> {

    private List<CofunderUserResource> assignedCofunders;

    public CofundersAvailableForApplicationPageResource() {
    }

    public CofundersAvailableForApplicationPageResource(long totalElements, int totalPages, List<CofunderUserResource> content, int number, int size, List<CofunderUserResource> assignedCofunders) {
        super(totalElements, totalPages, content, number, size);
        this.assignedCofunders = assignedCofunders;
    }

    public List<CofunderUserResource> getAssignedCofunders() {
        return assignedCofunders;
    }

    public void setAssignedCofunders(List<CofunderUserResource> assignedCofunders) {
        this.assignedCofunders = assignedCofunders;
    }
}
