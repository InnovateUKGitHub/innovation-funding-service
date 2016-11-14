package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.EligibilityForm;
import com.worth.ifs.competitionsetup.utils.CompetitionUtils;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;

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
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		
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
		
		competitionService.update(competition);
		
        return Collections.emptyList();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> objectId) {
		return performAutoSaveField(competitionResource, fieldName, value);
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return EligibilityForm.class.equals(clazz);
	}

	@Override
	public List<Error> updateCompetitionResourceWithAutoSave(List<Error> errors, CompetitionResource competitionResource, String fieldName, String value) throws ParseException {
		switch (fieldName) {
			case "multipleStream":
				competitionResource.setMultiStream(CompetitionUtils.textToBoolean(value));
				break;
			case "streamName":
				competitionResource.setStreamName(value);
				break;
			case "singleOrCollaborative":
				competitionResource.setCollaborationLevel(CollaborationLevel.fromCode(value));
				break;
			case "researchCategoryId":
				processResearchCategoryForAutoSave(value, competitionResource);
				break;
			case "leadApplicantType":
				competitionResource.setLeadApplicantType(LeadApplicantType.fromCode(value));
				break;
			case "researchParticipationAmountId":
				ResearchParticipationAmount amount = ResearchParticipationAmount.fromId(Integer.parseInt(value));
				if(amount != null) {
					competitionResource.setMaxResearchRatio(amount.getAmount());
				}
				break;
			case "resubmission":
				competitionResource.setResubmission(CompetitionUtils.textToBoolean(value));
				break;
			default:
				return asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
		}
		return errors;
	}

	private void processResearchCategoryForAutoSave(String inputValue, CompetitionResource competitionResource) throws ParseException {
		Long value = Long.parseLong(inputValue);
		if (competitionResource.getResearchCategories().contains(value)) {
			competitionResource.getResearchCategories().remove(value);
		} else {
			competitionResource.getResearchCategories().add(value);
		}
	}
}