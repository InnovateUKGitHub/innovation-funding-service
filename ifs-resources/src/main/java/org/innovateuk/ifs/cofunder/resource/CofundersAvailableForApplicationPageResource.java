package org.innovateuk.ifs.cofunder.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class CofundersAvailableForApplicationPageResource extends PageResource<CofuderUserResource> {

    private List<CofuderUserResource> assignedCofunders;

    public CofundersAvailableForApplicationPageResource() {
    }

    public CofundersAvailableForApplicationPageResource(long totalElements, int totalPages, List<CofuderUserResource> content, int number, int size, List<CofuderUserResource> assignedCofunders) {
        super(totalElements, totalPages, content, number, size);
        this.assignedCofunders = assignedCofunders;
    }

    public List<CofuderUserResource> getAssignedCofunders() {
        return assignedCofunders;
    }

    public void setAssignedCofunders(List<CofuderUserResource> assignedCofunders) {
        this.assignedCofunders = assignedCofunders;
    }
}
