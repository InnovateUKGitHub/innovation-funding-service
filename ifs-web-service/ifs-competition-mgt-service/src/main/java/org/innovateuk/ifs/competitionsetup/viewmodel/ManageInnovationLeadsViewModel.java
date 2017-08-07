package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Set;

public class ManageInnovationLeadsViewModel {

    private Long competitionId;
    private String competitionName;
    private String leadTechnologistName;
    private String innovationSectorName;
    private Set<String> innovationAreaNames;

    private List<UserResource> allInnovationLeads;
    private List<UserResource> innovationLeadsAssignedToCompetition;
    private List<UserResource> availableInnovationLeads;

    public ManageInnovationLeadsViewModel(Long competitionId, String competitionName,
                                          String leadTechnologistName, String innovationSectorName,
                                          Set<String> innovationAreaNames, List<UserResource> allInnovationLeads,
                                          List<UserResource> innovationLeadsAssignedToCompetition,
                                          List<UserResource> availableInnovationLeads) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.leadTechnologistName = leadTechnologistName;
        this.innovationSectorName = innovationSectorName;
        this.innovationAreaNames = innovationAreaNames;
        this.allInnovationLeads = allInnovationLeads;
        this.innovationLeadsAssignedToCompetition = innovationLeadsAssignedToCompetition;
        this.availableInnovationLeads = availableInnovationLeads;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getLeadTechnologistName() {
        return leadTechnologistName;
    }

    public String getInnovationSectorName() {
        return innovationSectorName;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
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
