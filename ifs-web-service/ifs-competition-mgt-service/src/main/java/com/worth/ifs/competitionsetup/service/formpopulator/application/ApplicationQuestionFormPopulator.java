package com.worth.ifs.competitionsetup.service.formpopulator.application;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.model.application.Question;
import com.worth.ifs.competitionsetup.service.formpopulator.CompetitionSetupSubsectionFormPopulator;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.inputsTypeMatching;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class ApplicationQuestionFormPopulator implements CompetitionSetupSubsectionFormPopulator {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private FormInputService formInputService;

	private Long appendixTypeId = 4L;
	private Long scoreTypeId = 23L;

	@Override
	public CompetitionSetupSubsection sectionToFill() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {
		ApplicationQuestionForm competitionSetupForm = new ApplicationQuestionForm();

		QuestionResource questionResource = questionService.getById(objectId.get());
		competitionSetupForm.setQuestion(initQuestionForForm(questionResource));

		return competitionSetupForm;
	}

	private Question initQuestionForForm(QuestionResource questionResource) {
		List<FormInputResource> formInputs = formInputService.findApplicationInputsByQuestion(questionResource.getId());
		List<FormInputResource> formAssessmentInputs = formInputService.findAssessmentInputsByQuestion(questionResource.getId());

		Boolean appendix = inputsTypeMatching(formInputs, appendixTypeId);
		Boolean scored = inputsTypeMatching(formAssessmentInputs, scoreTypeId);


		Optional<FormInputResource> foundInputs = formInputs.stream()
				.filter(formInputResource -> !formInputResource.getFormInputType().equals(appendixTypeId)).findAny();

		Question result = null;
		if(foundInputs.isPresent()) {
			result = createQuestionObjectFromQuestionResource(questionResource, foundInputs.get(), appendix, scored);
		}

		return result;
	}

	private Question createQuestionObjectFromQuestionResource(QuestionResource questionResource, FormInputResource formInputResource, Boolean appendix, Boolean scored){
		Question question = new Question();
		question.setId(questionResource.getId());
		question.setNumber(questionResource.getQuestionNumber());
		question.setShortTitle(questionResource.getShortName());
		question.setTitle(questionResource.getName());
		question.setSubTitle(questionResource.getDescription());

		question.setGuidanceTitle(formInputResource.getGuidanceQuestion());
		question.setGuidance(formInputResource.getGuidanceAnswer());
		if(formInputResource.getWordCount() > 0) {
			question.setMaxWords(formInputResource.getWordCount());
		} else {
			question.setMaxWords(400);
		}

		question.setScored(scored);
		question.setAppendix(appendix);

		return question;
	}

}
