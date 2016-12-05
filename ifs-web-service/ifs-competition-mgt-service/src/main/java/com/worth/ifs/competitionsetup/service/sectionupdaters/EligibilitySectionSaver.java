package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.EligibilityForm;
import com.worth.ifs.competitionsetup.utils.CompetitionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Competition setup section saver for the eligibility section.
 */
@Service
public class EligibilitySectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

	@Autowired
	private CompetitionService competitionService;
	
	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ELIGIBILITY;
	}

	@Override
	public ServiceResult<Void> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm, boolean allowInvalidData) {
		
		EligibilityForm eligibilityForm = (EligibilityForm) competitionSetupForm;
		
		competition.setResearchCategories(eligibilityForm.getResearchCategoryId());
		
		ResearchParticipationAmount amount = ResearchParticipationAmount.fromId(eligibilityForm.getResearchParticipationAmountId());
		if(amount != null) {
			competition.setMaxResearchRatio(amount.getAmount());
		}
		
		boolean multiStream = "yes".equals(eligibilityForm.getMultipleStream());
		competition.setMultiStream(multiStream);
		if(multiStream) {
			competition.setStreamName(eligibilityForm.getStreamName());
		} else {
			competition.setStreamName(null);
		}

		competition.setResubmission(CompetitionUtils.textToBoolean(eligibilityForm.getResubmission()));

		CollaborationLevel level = CollaborationLevel.fromCode(eligibilityForm.getSingleOrCollaborative());
		competition.setCollaborationLevel(level);
		
		LeadApplicantType type = LeadApplicantType.fromCode(eligibilityForm.getLeadApplicantType());
		competition.setLeadApplicantType(type);
		
		return competitionService.update(competition);
	}

	@Override
	protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource, String fieldName, String value) {
        if("researchCategoryId".equals(fieldName)) {
            processResearchCategoryForAutoSave(value, competitionResource);
            return competitionService.update(competitionResource);
        }
		return super.handleIrregularAutosaveCase(competitionResource, fieldName, value);
	}

	private void processResearchCategoryForAutoSave(String inputValue, CompetitionResource competitionResource) {
        Long value = Long.parseLong(inputValue);
        if (competitionResource.getResearchCategories().contains(value)) {
			competitionResource.getResearchCategories().remove(value);
        } else {
			competitionResource.getResearchCategories().add(value);
        }
    }

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return EligibilityForm.class.equals(clazz);
	}

}