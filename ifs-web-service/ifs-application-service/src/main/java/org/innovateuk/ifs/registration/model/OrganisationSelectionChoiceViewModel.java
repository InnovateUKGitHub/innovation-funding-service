package org.innovateuk.ifs.registration.model;

public class OrganisationSelectionChoiceViewModel {

    private final long id;
    private final String name;
    private final String type;
    private final boolean eligibleToLead;

    public OrganisationSelectionChoiceViewModel(long id, String name, String type, boolean eligibleToLead) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.eligibleToLead = eligibleToLead;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isEligibleToLead() {
        return eligibleToLead;
    }
}
