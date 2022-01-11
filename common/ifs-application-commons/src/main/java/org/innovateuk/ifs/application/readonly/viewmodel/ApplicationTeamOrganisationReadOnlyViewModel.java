package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;

import java.util.List;

public class ApplicationTeamOrganisationReadOnlyViewModel {
    private final String name;
    private final String type;
    private final List<ApplicationTeamUserReadOnlyViewModel> users;
    private final boolean existing;
    private final AddressResource address;

    public ApplicationTeamOrganisationReadOnlyViewModel(String name, String type, List<ApplicationTeamUserReadOnlyViewModel> users, boolean existing, AddressResource address) {
        this.name = name;
        this.type = type;
        this.users = users;
        this.existing = existing;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<ApplicationTeamUserReadOnlyViewModel> getUsers() {
        return users;
    }

    public boolean isExisting() {
        return existing;
    }

    public AddressResource getAddress() {
        return address;
    }

    public boolean isLead() {
        return users.stream().anyMatch(ApplicationTeamUserReadOnlyViewModel::isLead);
    }

}
