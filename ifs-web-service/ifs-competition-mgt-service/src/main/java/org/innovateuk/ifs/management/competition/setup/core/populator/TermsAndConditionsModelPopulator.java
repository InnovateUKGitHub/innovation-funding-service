package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.TermsAndConditionsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Populates the model for the terms and condition competition setup section.
 */
@Service
public class TermsAndConditionsModelPopulator {

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Autowired
    private CompetitionSetupPopulator competitionSetupPopulator;

    public TermsAndConditionsViewModel populateModel(CompetitionResource competitionResource, UserResource userResource, boolean stateAidPage) {

        GrantTermsAndConditionsResource termsAndConditions = null;
        if (competitionResource.getTermsAndConditions() != null) {
            termsAndConditions = termsAndConditionsRestService.getById(competitionResource.getTermsAndConditions().getId()).getSuccess();
        }

        GeneralSetupViewModel generalViewModel = competitionSetupPopulator.populateGeneralModelAttributes(competitionResource, userResource, CompetitionSetupSection.TERMS_AND_CONDITIONS);
        List<GrantTermsAndConditionsResource> termsAndConditionsList = termsAndConditionsRestService
                .getLatestVersionsForAllTermsAndConditions()
                .getSuccess();

        boolean termsAndConditionsDocUploaded = competitionResource.isCompetitionTermsUploaded();

        boolean includeStateAid = FundingRules.SUBSIDY_CONTROL == competitionResource.getFundingRules() && !competitionResource.isExpressionOfInterest();

        GrantTermsAndConditionsResource otherTermsAndConditions = null;

        if (includeStateAid && competitionResource.getOtherFundingRulesTermsAndConditions() != null) {
            otherTermsAndConditions = termsAndConditionsRestService.getById(competitionResource.getOtherFundingRulesTermsAndConditions().getId()).getSuccess();
        }

        return new TermsAndConditionsViewModel(generalViewModel, termsAndConditionsList,
                termsAndConditions, otherTermsAndConditions, termsAndConditionsDocUploaded, includeStateAid, stateAidPage);
    }

}