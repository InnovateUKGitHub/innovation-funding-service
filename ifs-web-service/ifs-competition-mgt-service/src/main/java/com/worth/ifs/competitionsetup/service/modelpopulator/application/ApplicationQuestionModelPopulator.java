package com.worth.ifs.competitionsetup.service.modelpopulator.application;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSubsectionModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

/**
 * populates the model for the initial details competition setup section.
 */
@Service
public class ApplicationQuestionModelPopulator implements CompetitionSetupSubsectionModelPopulator {

    @Override
	public CompetitionSetupSubsection sectionToPopulateModel() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Autowired
    public QuestionService questionService;

    @Autowired
    public SectionService sectionService;

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource, Optional<Long> objectId) {
        objectId.ifPresent(objectIdLong ->
                model.addAttribute("usingAppendix", checkUsingAppendix(questionService.getById(objectIdLong))));
	}

	private SectionResource getSectionToCheck(Long sectionId) {
        SectionResource sectionResource = sectionService.getById(sectionId);
        if(null != sectionResource.getParentSection()) {
            return getSectionToCheck(sectionResource.getParentSection());
        }
        return sectionResource;
    }

	private Boolean checkUsingAppendix(QuestionResource questionResource) {
        SectionResource sectionToCheck = getSectionToCheck(questionResource.getSection());

    	return !sectionToCheck.getName().equals("Project details");
	}
}
