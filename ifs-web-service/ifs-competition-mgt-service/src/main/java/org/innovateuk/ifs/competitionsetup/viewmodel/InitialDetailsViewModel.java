package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class InitialDetailsViewModel extends CompetitionSetupViewModel {
    private List<UserResource> competitionExecutiveUsers;
    private List<InnovationSectorResource> innovationSectors;
    private List<InnovationAreaResource> innovationAreas;
    private List<CompetitionTypeResource> competitionTypes;
    private List<UserResource> innovationLeadTechUsers;

    public InitialDetailsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                   List<UserResource> competitionExecutiveUsers, List<InnovationSectorResource> innovationSectors,
                                   List<InnovationAreaResource> innovationAreas, List<CompetitionTypeResource> competitionTypes,
                                   List<UserResource> innovationLeadTechUsers) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.competitionExecutiveUsers = competitionExecutiveUsers;
        this.innovationSectors = innovationSectors;
        this.innovationAreas = innovationAreas;
        this.competitionTypes = competitionTypes;
        this.innovationLeadTechUsers = innovationLeadTechUsers;
    }

    public List<UserResource> getCompetitionExecutiveUsers() {
        return competitionExecutiveUsers;
    }

    public List<InnovationSectorResource> getInnovationSectors() {
        return innovationSectors;
    }

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public List<CompetitionTypeResource> getCompetitionTypes() {
        return competitionTypes;
    }

    public List<UserResource> getInnovationLeadTechUsers() {
        return innovationLeadTechUsers;
    }
}
