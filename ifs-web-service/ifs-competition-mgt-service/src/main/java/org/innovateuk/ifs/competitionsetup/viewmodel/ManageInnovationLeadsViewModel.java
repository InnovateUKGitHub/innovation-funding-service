package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class ManageInnovationLeadsViewModel {

    private List<UserResource> allInnovationLeads;
    private List<UserResource> innovationLeadsAssignedToCompetition;
    private List<UserResource> availableInnovationLeads;

    public ManageInnovationLeadsViewModel(List<UserResource> allInnovationLeads,
                                          List<UserResource> innovationLeadsAssignedToCompetition,
                                          List<UserResource> availableInnovationLeads) {
        this.allInnovationLeads = allInnovationLeads;
        this.innovationLeadsAssignedToCompetition = innovationLeadsAssignedToCompetition;
        this.availableInnovationLeads = availableInnovationLeads;
    }

    public List<UserResource> getAllInnovationLeads() {
        return allInnovationLeads;
    }

    public List<UserResource> getInnovationLeadsAssignedToCompetition() {
        return innovationLeadsAssignedToCompetition;
    }

    public List<UserResource> getAvailableInnovationLeads() {
        return availableInnovationLeads;
    }
}
