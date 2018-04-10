package org.innovateuk.ifs.competitionsetup.service.formpopulator.application;

import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationProjectForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.CompetitionSetupSubsectionFormPopulator;
import org.innovateuk.ifs.question.service.controller.service.QuestionSetupCompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class ApplicationProjectFormPopulator implements CompetitionSetupSubsectionFormPopulator {

	@Autowired
	private QuestionSetupCompetitionService questionSetupCompetitionService;

	@Override
	public CompetitionSetupSubsection sectionToFill() {
		return CompetitionSetupSubsection.PROJECT_DETAILS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {

		ApplicationProjectForm competitionSetupForm = new ApplicationProjectForm();

		if(objectId.isPresent()) {
			CompetitionSetupQuestionResource questionResource = questionSetupCompetitionService.getQuestion(objectId.get()).getSuccess();
			competitionSetupForm.setQuestion(questionResource);


		} else {
            throw new ObjectNotFoundException();
        }

		return competitionSetupForm;
	}
}
