package org.innovateuk.ifs.competitionsetup.eligibility.populator;

import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.competitionsetup.core.util.CompetitionUtils;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Form populator for the eligibility competition setup section.
 */
@Service
public class EligibilityFormPopulator implements CompetitionSetupFormPopulator {

    private CompetitionRestService competitionRestService;
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    public EligibilityFormPopulator(CompetitionRestService competitionRestService,
                                    GrantClaimMaximumRestService grantClaimMaximumRestService) {
        this.competitionRestService = competitionRestService;
        this.grantClaimMaximumRestService = grantClaimMaximumRestService;
    }

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.ELIGIBILITY;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        EligibilityForm competitionSetupForm = new EligibilityForm();

        competitionSetupForm.setResearchCategoryId(competitionResource.getResearchCategories());

        ResearchParticipationAmount amount = ResearchParticipationAmount.fromAmount(competitionResource.getMaxResearchRatio());
        if (amount != null) {
            competitionSetupForm.setResearchParticipationAmountId(amount.getId());
        }

        competitionSetupForm.setMultipleStream("no");

        CollaborationLevel level = competitionResource.getCollaborationLevel();
        if (level != null) {
            competitionSetupForm.setSingleOrCollaborative(level.getCode());
        }

        List<Long> organisationTypes = competitionResource.getLeadApplicantTypes();
        if (organisationTypes != null) {
            competitionSetupForm.setLeadApplicantTypes(organisationTypes);
        }

        competitionSetupForm.setResubmission(CompetitionUtils.booleanToText(competitionResource.getResubmission()));

        Boolean overrideFundingRuleSet = getOverrideFundingRulesSet(competitionResource, competitionSetupForm);
        competitionSetupForm.setOverrideFundingRules(overrideFundingRuleSet);

        if (overrideFundingRuleSet != null && overrideFundingRuleSet) {
            competitionSetupForm.setFundingLevelPercentage(getFundingLevelPercentage(competitionResource));
        }

        return competitionSetupForm;
    }

    private Boolean getOverrideFundingRulesSet(CompetitionResource competitionResource,
                                               EligibilityForm eligibilityForm) {
        if(isFirstTimeInForm(eligibilityForm)) {
            return fundingRulesAreOverriden(competitionResource);
        }

        return null;
    }

    private boolean isFirstTimeInForm(EligibilityForm eligibilityForm) {
        return (eligibilityForm.getMultipleStream() != null) &&
                (!eligibilityForm.getResearchCategoryId().isEmpty() && eligibilityForm.getResearchCategoryId() != null) &&
                ( eligibilityForm.getSingleOrCollaborative() != null) &&
                (!eligibilityForm.getLeadApplicantTypes().isEmpty() && eligibilityForm.getLeadApplicantTypes() != null) &&
                (eligibilityForm.getResubmission() != null);
    }

    private boolean fundingRulesAreOverriden(CompetitionResource competitionResource) {
        CompetitionResource template = competitionRestService.findTemplateCompetitionForCompetitionType(
                competitionResource.getCompetitionType()).getSuccess();

        Set<Long> currentGrantClaimMaximums = competitionResource.getGrantClaimMaximums();
        Set<Long> templateGrantClaimMaximums = template.getGrantClaimMaximums();
        return !currentGrantClaimMaximums.equals(templateGrantClaimMaximums);
    }

    private Integer getFundingLevelPercentage(CompetitionResource competition) {
        Optional<GrantClaimMaximumResource> overriddenGcm = competition.getGrantClaimMaximums()
                .stream()
                .map(id -> grantClaimMaximumRestService.getGrantClaimMaximumById(id).getSuccess())
                .filter(gcm -> gcm.getOrganisationType().getId().equals(OrganisationTypeEnum.BUSINESS.getId()))
                .findFirst();

        return overriddenGcm.get().getMaximum();
    }

}
