package org.innovateuk.ifs.registration.viewmodel;

public class OrganisationSelectionChoiceViewModel {

    private final long id;
    private final String name;
    private final String type;

    public OrganisationSelectionChoiceViewModel(long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
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

}
