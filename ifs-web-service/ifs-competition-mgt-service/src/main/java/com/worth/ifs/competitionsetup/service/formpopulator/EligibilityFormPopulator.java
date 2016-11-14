package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.EligibilityForm;
import com.worth.ifs.competitionsetup.utils.CompetitionUtils;
import org.springframework.stereotype.Service;

/**
 * Form populator for the eligibility competition setup section.
 */
@Service
public class EligibilityFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ELIGIBILITY;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		EligibilityForm competitionSetupForm = new EligibilityForm();
		
		competitionSetupForm.setResearchCategoryId(competitionResource.getResearchCategories());
		
		ResearchParticipationAmount amount = ResearchParticipationAmount.fromAmount(competitionResource.getMaxResearchRatio());
		if(amount != null) {
			competitionSetupForm.setResearchParticipationAmountId(amount.getId());
		}

		CollaborationLevel level = competitionResource.getCollaborationLevel();
		if(level != null) {
			competitionSetupForm.setSingleOrCollaborative(level.getCode());
		}
		
		LeadApplicantType type = competitionResource.getLeadApplicantType();
		if(type != null) {
			competitionSetupForm.setLeadApplicantType(type.getCode());
		}

        competitionSetupForm.setResubmission(CompetitionUtils.booleanToText(competitionResource.getResubmission()));

		return competitionSetupForm;
	}


}
