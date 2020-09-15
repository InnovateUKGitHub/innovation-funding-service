package org.innovateuk.ifs.management.competition.setup.initialdetail.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Set;

public class ManageInnovationLeadsViewModel extends CompetitionSetupViewModel {

    private Long competitionId;
    private String competitionName;
    private String leadTechnologistName;
    private String executiveName;
    private String innovationSectorName;
    private Set<String> innovationAreaNames;

    private List<UserResource> innovationLeadsAssignedToCompetition;
    private List<UserResource> availableInnovationLeads;

    public ManageInnovationLeadsViewModel(Long competitionId, String competitionName,
                                          String leadTechnologistName, String executiveName,
                                          String innovationSectorName, Set<String> innovationAreaNames,
                                          List<UserResource> availableInnovationLeads,
                                          List<UserResource> innovationLeadsAssignedToCompetition) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.leadTechnologistName = leadTechnologistName;
        this.executiveName = executiveName;
        this.innovationSectorName = innovationSectorName;
        this.innovationAreaNames = innovationAreaNames;

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

    public String getExecutiveName() {
        return executiveName;
    }

    public String getInnovationSectorName() {
        return innovationSectorName;
    }

    public Set<String> getInnovationAreaNames() {
        return innovationAreaNames;
    }

    public List<UserResource> getInnovationLeadsAssignedToCompetition() {
        return innovationLeadsAssignedToCompetition;
    }

    public List<UserResource> getAvailableInnovationLeads() {
        return availableInnovationLeads;
    }
}
