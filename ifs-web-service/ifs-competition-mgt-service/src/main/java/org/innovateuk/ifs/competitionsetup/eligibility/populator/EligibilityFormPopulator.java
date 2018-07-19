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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Form populator for the eligibility competition setup section.
 */
@Service
public class EligibilityFormPopulator implements CompetitionSetupFormPopulator {

    private CompetitionRestService competitionRestService;

    public EligibilityFormPopulator(CompetitionRestService competitionRestService) {
        this.competitionRestService = competitionRestService;
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

        competitionSetupForm.setOverrideFundingRules(getOverrideFundingRules(competitionResource));
        competitionSetupForm.setResubmission(CompetitionUtils.booleanToText(competitionResource.getResubmission()));

        return competitionSetupForm;
    }

    private boolean getOverrideFundingRules(CompetitionResource competitionResource) {
        CompetitionResource template = competitionRestService.findTemplateCompetitionForCompetitionType(
                competitionResource.getCompetitionType()).getSuccess();

        Set<Long> currentGrantClaimMaximums = competitionResource.getGrantClaimMaximums();
        Set<Long> templateGrantClaimMaximums = template.getGrantClaimMaximums();
        return currentGrantClaimMaximums.equals(templateGrantClaimMaximums);
    }

}
