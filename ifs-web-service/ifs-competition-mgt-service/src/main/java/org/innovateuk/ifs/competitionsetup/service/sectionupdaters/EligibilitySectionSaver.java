package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.EligibilityForm;
import org.innovateuk.ifs.competitionsetup.utils.CompetitionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Competition setup section saver for the eligibility section.
 */
@Service
public class EligibilitySectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

	public static final String RESEARCH_CATEGORY_ID = "researchCategoryId";
	public static final String LEAD_APPLICANT_TYPES = "leadApplicantTypes";

	@Autowired
	private CompetitionService competitionService;
	
	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ELIGIBILITY;
	}

	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		
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
		
		competition.setLeadApplicantTypes(eligibilityForm.getLeadApplicantTypes());
		
		return competitionService.update(competition);
	}

	@Override
	protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> questionId) {
        if(RESEARCH_CATEGORY_ID.equals(fieldName)) {
            removeIfPresentAddIfNot(value, competitionResource.getResearchCategories());
            return competitionService.update(competitionResource);
        }
        if(LEAD_APPLICANT_TYPES.equals(fieldName)){
			removeIfPresentAddIfNot(value, competitionResource.getLeadApplicantTypes());
			return competitionService.update(competitionResource);
		}
		return super.handleIrregularAutosaveCase(competitionResource, fieldName, value, questionId);
	}


	private void removeIfPresentAddIfNot(String inputValue, Collection<Long> collection){
		Long value = Long.parseLong(inputValue);
		if (collection.contains(value)) {
			collection.remove(value);
		} else {
			collection.add(value);
		}
	}



	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return EligibilityForm.class.equals(clazz);
	}

}
