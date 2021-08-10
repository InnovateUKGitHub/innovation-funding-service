package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Form populator for the terms and conditions competition setup section.
 */
@Service
public class TermsAndConditionsFormPopulator {
    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;
    private static final String PROJECT_COST_GUIDANCE_HTTP = "http://";


    public TermsAndConditionsForm populateForm(CompetitionResource competitionResource) {
        TermsAndConditionsForm termsAndConditionsForm = new TermsAndConditionsForm();
        if (competitionResource.getTermsAndConditions() != null) {
            termsAndConditionsForm.setTermsAndConditionsId(competitionResource.getTermsAndConditions().getId());
                CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = competitionResource.getCompetitionThirdPartyConfigResource();
                if (competitionThirdPartyConfigResource != null) {
                    if (isProcurementThirdParty(termsAndConditionsForm.getTermsAndConditionsId())) {
                        termsAndConditionsForm.setThirdPartyTermsAndConditionsLabel(competitionThirdPartyConfigResource.getTermsAndConditionsLabel());
                        termsAndConditionsForm.setThirdPartyTermsAndConditionsText(competitionThirdPartyConfigResource.getTermsAndConditionsGuidance());
                        termsAndConditionsForm.setProjectCostGuidanceLink(competitionThirdPartyConfigResource.getProjectCostGuidanceUrl());
                    }
                    prePopulateWithHttp(termsAndConditionsForm, competitionThirdPartyConfigResource);
                }
            }
        return termsAndConditionsForm;
    }

    public TermsAndConditionsForm populateFormForStateAid(CompetitionResource competitionResource) {
        TermsAndConditionsForm termsAndConditionsForm = new TermsAndConditionsForm();
        if (competitionResource.getOtherFundingRulesTermsAndConditions() != null) {
            termsAndConditionsForm.setTermsAndConditionsId(competitionResource.getOtherFundingRulesTermsAndConditions().getId());
        }
        return termsAndConditionsForm;
    }

    public void populateThirdPartyConfigData(TermsAndConditionsForm termsAndConditionsForm, CompetitionResource competition) {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = competition.getCompetitionThirdPartyConfigResource();
        competitionThirdPartyConfigResource.setTermsAndConditionsLabel(termsAndConditionsForm.getThirdPartyTermsAndConditionsLabel());
        competitionThirdPartyConfigResource.setTermsAndConditionsGuidance(termsAndConditionsForm.getThirdPartyTermsAndConditionsText());
        competitionThirdPartyConfigResource.setProjectCostGuidanceUrl(termsAndConditionsForm.getProjectCostGuidanceLink());
        competition.setCompetitionThirdPartyConfigResource(competitionThirdPartyConfigResource);
    }

    private boolean isProcurementThirdParty(long termsAndConditionsId) {
        return termsAndConditionsRestService.getById(termsAndConditionsId).getSuccess().isProcurementThirdParty();
    }

    private void prePopulateWithHttp(TermsAndConditionsForm termsAndConditionsForm, CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource) {
        if(competitionThirdPartyConfigResource.getProjectCostGuidanceUrl() == null || competitionThirdPartyConfigResource.getProjectCostGuidanceUrl().isEmpty()) {
            termsAndConditionsForm.setProjectCostGuidanceLink(PROJECT_COST_GUIDANCE_HTTP);
        }
    }
}
