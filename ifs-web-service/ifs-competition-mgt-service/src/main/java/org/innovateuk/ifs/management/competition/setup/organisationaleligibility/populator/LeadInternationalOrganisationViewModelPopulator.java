package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.viewmodel.LeadInternationalOrganisationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeadInternationalOrganisationViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    public LeadInternationalOrganisationViewModel populateModel(long competitionId, CompetitionOrganisationConfigResource competitionOrganisationConfigResource) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return new LeadInternationalOrganisationViewModel(competition, competitionOrganisationConfigResource.getInternationalLeadOrganisationAllowed());
    }
}