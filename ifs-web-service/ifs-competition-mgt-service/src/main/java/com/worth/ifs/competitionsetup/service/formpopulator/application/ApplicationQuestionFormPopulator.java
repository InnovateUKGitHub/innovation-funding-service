package com.worth.ifs.competitionsetup.service.formpopulator.application;

import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.error.exception.*;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.*;
import com.worth.ifs.competitionsetup.form.application.*;
import com.worth.ifs.competitionsetup.service.*;
import com.worth.ifs.competitionsetup.service.formpopulator.*;
import com.worth.ifs.form.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class ApplicationQuestionFormPopulator implements CompetitionSetupSubsectionFormPopulator {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private FormInputService formInputService;

	@Autowired
	private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSubsection sectionToFill() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {
		ApplicationQuestionForm competitionSetupForm = new ApplicationQuestionForm();

		if(objectId.isPresent()) {
            QuestionResource questionResource = questionService.getById(objectId.get());
            competitionSetupForm.setQuestion(initQuestionForForm(questionResource));
        } else {
            throw new ObjectNotFoundException();
        }

		return competitionSetupForm;
	}

	private CompetitionSetupQuestionResource initQuestionForForm(QuestionResource questionResource) {
		return competitionSetupQuestionService.getQuestion(questionResource.getId()).getSuccessObjectOrThrowException();
	}



}
