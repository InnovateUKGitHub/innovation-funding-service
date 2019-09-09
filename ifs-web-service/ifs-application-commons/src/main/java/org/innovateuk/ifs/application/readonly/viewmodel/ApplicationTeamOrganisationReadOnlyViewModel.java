package org.innovateuk.ifs.application.readonly.viewmodel;

import java.util.List;

public class ApplicationTeamOrganisationReadOnlyViewModel {
    private final String name;
    private final String type;
    private final List<ApplicationTeamUserReadOnlyViewModel> users;
    private final boolean existing;

    public ApplicationTeamOrganisationReadOnlyViewModel(String name, String type, List<ApplicationTeamUserReadOnlyViewModel> users, boolean existing) {
        this.name = name;
        this.type = type;
        this.users = users;
        this.existing = existing;
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

    public boolean isLead() {
        return users.stream().anyMatch(ApplicationTeamUserReadOnlyViewModel::isLead);
    }

}
