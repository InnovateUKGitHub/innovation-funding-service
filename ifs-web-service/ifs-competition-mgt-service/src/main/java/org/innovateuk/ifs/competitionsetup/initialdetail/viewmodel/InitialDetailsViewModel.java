package org.innovateuk.ifs.competitionsetup.initialdetail.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

import static java.util.Arrays.asList;

public class InitialDetailsViewModel extends CompetitionSetupViewModel {
    private final List<UserResource> competitionExecutiveUsers;
    private final List<InnovationSectorResource> innovationSectors;
    private final List<InnovationAreaResource> innovationAreas;
    private final List<CompetitionTypeResource> competitionTypes;
    private final List<UserResource> innovationLeadTechUsers;
    private final List<FundingType> fundingTypes;
    private final boolean restricted;

    public InitialDetailsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                   List<UserResource> competitionExecutiveUsers, List<InnovationSectorResource> innovationSectors,
                                   List<InnovationAreaResource> innovationAreas, List<CompetitionTypeResource> competitionTypes,
                                   List<UserResource> innovationLeadTechUsers, boolean restricted) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.competitionExecutiveUsers = competitionExecutiveUsers;
        this.innovationSectors = innovationSectors;
        this.innovationAreas = innovationAreas;
        this.competitionTypes = competitionTypes;
        this.innovationLeadTechUsers = innovationLeadTechUsers;
        this.fundingTypes = asList(FundingType.values());
        this.restricted = restricted;
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

    public List<FundingType> getFundingTypes() {
        return fundingTypes;
    }

    public boolean restricted() {
        return restricted || generalSetupViewModel.getCompetition().isSetupAndLive();
    }
}
