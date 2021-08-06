package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.viewmodel.CompetitionTermsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Populator for creating the {@link org.innovateuk.ifs.competition.viewmodel.CompetitionTermsViewModel}
 */
@Service
public class CompetitionTermsAndConditionsPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    public CompetitionTermsViewModel populate(long competitionId) {
        CompetitionTermsViewModel competitionTermsViewModel;

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        GrantTermsAndConditionsResource grantTermsAndConditionsResource = competition.getTermsAndConditions();

        if (grantTermsAndConditionsResource.isProcurementThirdParty()) {
            competitionTermsViewModel = new CompetitionTermsViewModel(competitionId,
                    grantTermsAndConditionsResource,
                    competition.getCompetitionTerms(),
                    competition.getCompetitionThirdPartyConfigResource());
        } else {
            competitionTermsViewModel = new CompetitionTermsViewModel(competitionId, grantTermsAndConditionsResource);
        }

        return competitionTermsViewModel;
    }
}
