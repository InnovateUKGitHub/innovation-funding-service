package org.innovateuk.ifs.project.financechecks.viewmodel;

public class ProjectOrganisationRowViewModel {

    private final Long id;
    private final String name;
    private final boolean lead;

    public ProjectOrganisationRowViewModel(Long id, String name, boolean lead) {
        this.id = id;
        this.name = name;
        this.lead = lead;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isLead() {
        return lead;
    }
}
