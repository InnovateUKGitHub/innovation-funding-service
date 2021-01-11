package org.innovateuk.ifs.management.competition.setup.projecteligibility.populator;

import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.management.competition.setup.core.util.CompetitionUtils;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.form.ProjectEligibilityForm;
import org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Form populator for the eligibility competition setup section.
 */
@Service
public class ProjectEligibilityFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.PROJECT_ELIGIBILITY;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();

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

        competitionSetupForm.setKtpCompetition(competitionResource.isKtp());

        return competitionSetupForm;
    }
}
