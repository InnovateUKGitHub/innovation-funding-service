package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.EligibilityForm;
import com.worth.ifs.controller.form.enumerable.ResearchParticipationAmount;

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
		
		if(competitionResource.isMultiStream()) {
			competitionSetupForm.setMultipleStream("yes");
		} else {
			competitionSetupForm.setMultipleStream("no");
		}
		
		CollaborationLevel level = competitionResource.getCollaborationLevel();
		if(level != null) {
			competitionSetupForm.setSingleOrCollaborative(level.getCode());
		}
		
		LeadApplicantType type = competitionResource.getLeadApplicantType();
		if(type != null) {
			competitionSetupForm.setLeadApplicantType(type.getCode());
		}

		return competitionSetupForm;
	}

}
