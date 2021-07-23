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

    public CompetitionTermsViewModel populate( long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        GrantTermsAndConditionsResource termsAndConditions = competition.getTermsAndConditions();

        return new CompetitionTermsViewModel(competitionId,
                termsAndConditions,
                competition.getTermsAndConditionsLabel(),
                competition.getTermsAndConditionsGuidance(),
                competition.getProjectCostGuidanceUrl());
    }
}
