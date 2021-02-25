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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Populates the model for the terms and condition competition setup section.
 */
@Service
public class TermsAndConditionsModelPopulator {

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Autowired
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Value("${ifs.subsidy.control.northern.ireland.enabled:false}")
    private Boolean subsidyControlNorthernIrelandEnabled;

    public TermsAndConditionsViewModel populateModel(CompetitionResource competitionResource, UserResource userResource, boolean stateAidPage) {

        GrantTermsAndConditionsResource termsAndConditions = null;
        if (competitionResource.getTermsAndConditions() != null) {
            termsAndConditions = termsAndConditionsRestService.getById(competitionResource.getTermsAndConditions().getId()).getSuccess();
        }

        GeneralSetupViewModel generalViewModel = competitionSetupPopulator.populateGeneralModelAttributes(competitionResource, userResource, CompetitionSetupSection.TERMS_AND_CONDITIONS);

        boolean termsAndConditionsDocUploaded = competitionResource.isCompetitionTermsUploaded();

        boolean includeStateAid = includeStateAid(competitionResource);

        List<GrantTermsAndConditionsResource> termsAndConditionsList = termsAndConditionsRestService
                .getLatestVersionsForAllTermsAndConditions()
                .getSuccess();

        if (includeStateAid) {
            termsAndConditionsList = termsAndConditionsList.stream().filter(tandc -> !tandc.isProcurement()).collect(Collectors.toList());
        }

        GrantTermsAndConditionsResource otherTermsAndConditions = null;
        if (includeStateAid && competitionResource.getOtherFundingRulesTermsAndConditions() != null) {
            otherTermsAndConditions = termsAndConditionsRestService.getById(competitionResource.getOtherFundingRulesTermsAndConditions().getId()).getSuccess();
        }

        return new TermsAndConditionsViewModel(generalViewModel, termsAndConditionsList,
                termsAndConditions, otherTermsAndConditions, termsAndConditionsDocUploaded, includeStateAid, stateAidPage);
    }

    private boolean includeStateAid(CompetitionResource competitionResource) {
        return subsidyControlNorthernIrelandEnabled
                && FundingRules.SUBSIDY_CONTROL == competitionResource.getFundingRules()
                && !competitionResource.isExpressionOfInterest();
    }

}