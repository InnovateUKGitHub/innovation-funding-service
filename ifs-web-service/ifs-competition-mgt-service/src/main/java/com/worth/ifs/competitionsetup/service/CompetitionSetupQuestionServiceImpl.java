package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.competitionsetup.viewmodel.application.QuestionViewModel;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.service.FormInputService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetitionSetupQuestionServiceImpl implements CompetitionSetupQuestionService {

	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

	@Autowired
	private QuestionService questionService;

	@Autowired
	private FormInputService formInputService;

    private final Long fileUploadId = 4L;
    private final Long assessorScoreId = 23L;


    @Override
    public QuestionViewModel getQuestion(final Long questionId) {
        QuestionResource questionResource = questionService.getById(questionId);

        QuestionViewModel question = new QuestionViewModel();
        List<FormInputResource> formInputResources = formInputService.findApplicationInputsByQuestion(questionId);
        //TODO AssessorScore for application questions
        //List<FormInputResource> formInputAssessmentResources = formInputService.findAssessmentInputsByQuestion(questionId);

        question.setId(questionResource.getId());
        question.setTitle(questionResource.getName());
        question.setSubTitle(questionResource.getDescription());

        Optional<FormInputResource> result = formInputResources.stream().filter(formInput -> !formInput.getFormInputType().equals(4L)).findFirst();
        if(result.isPresent()) {
            FormInputResource formInputResource = result.get();
            question.setGuidanceTitle(formInputResource.getGuidanceQuestion());
            question.setGuidance(formInputResource.getGuidanceAnswer());
            question.setMaxWords(formInputResource.getWordCount());
        }

        question.setAppendix(hasAppendix(formInputResources));
        //TODO AssessorScore for application questions
        //question.setScored(hasAssessorScore(formInputAssessmentResources));

        return question;
    }

    @Override
	public void updateQuestion(QuestionViewModel question) {
		QuestionResource questionResource = questionService.getById(question.getId());
		List<FormInputResource> formInputResources = formInputService.findApplicationInputsByQuestion(question.getId());
        //TODO AssessorScore for application questions
        //List<FormInputResource> formInputAssessmentResources = formInputService.findAssessmentInputsByQuestion(question.getId());

		questionResource.setName(question.getTitle());
        questionResource.setDescription(question.getSubTitle());
        questionService.save(questionResource);

        FormInputResource formInputResource = new FormInputResource();
        Optional<FormInputResource> result = formInputResources.stream().filter(formInput -> !formInput.getFormInputType().equals(4L)).findFirst();
        if(result.isPresent()) {
            formInputResource = result.get();
        } else {
            formInputResource.setQuestion(question.getId());
            formInputResource.setScope(FormInputScope.APPLICATION);
            formInputResource.setFormInputType(2L);
        }

        formInputResource.setGuidanceQuestion(question.getGuidanceTitle());
        formInputResource.setGuidanceAnswer(question.getGuidance());
        formInputResource.setWordCount(question.getMaxWords());
        formInputService.save(formInputResource);

        handleAppendix(question, formInputResources, questionResource, formInputResource);
        //TODO AssessorScore for application questions
        //handleAssessorScore(question, formInputAssessmentResources, questionResource, formInputResource);
	}

	private void handleAppendix(QuestionViewModel question, List<FormInputResource> formInputResources, QuestionResource questionResource, FormInputResource formInputResource) {
	    if(question.getAppendix()) {
            //check if it's there otherwise add
            if(!hasAppendix(formInputResources)) {
                FormInputResource appendix = new FormInputResource();
                appendix.setFormInputType(fileUploadId);
                appendix.setGuidanceQuestion("What should I include in the appendix?");
                appendix.setGuidanceAnswer("<p>You may include an appendix of additional information to support the question.</p>" +
                        "<p>You may include, for example, a Gantt chart or project management structure.</p>" +
                        "<p>The appendix should:</p>" +
                        "<ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li>" +
                        "<li>be readable with 100% magnification</li>" +
                        "<li>contain your application number and project title at the top</li>" +
                        "<li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><" +
                        "li>be less than 1mb in size</li>" +
                        "</ul>");
                appendix.setDescription("Appendix");
                appendix.setIncludedInApplicationSummary(true);
                appendix.setQuestion(questionResource.getId());
                appendix.setCompetition(formInputResource.getCompetition());
                appendix.setScope(FormInputScope.APPLICATION);
                if(formInputResource.getPriority() != null) {
                    appendix.setPriority(formInputResource.getPriority() + 1);
                } else {
                    appendix.setPriority(0);
                }
                appendix.setWordCount(null);

                formInputService.save(appendix);
            }
        } else {
            if(hasAppendix(formInputResources)) {
                Long appendixId = formInputResources
                        .stream()
                        .filter(formInput -> formInput.getFormInputType().equals(fileUploadId)).findFirst()
                        .get()
                        .getId();
                formInputService.delete(appendixId);
            }
        }

    }

	private Boolean hasAppendix(List<FormInputResource> formInputResources) {
        return checkFormInputsForType(formInputResources, fileUploadId);
    }

    private void handleAssessorScore(QuestionViewModel question, List<FormInputResource> formInputResources, QuestionResource questionResource, FormInputResource formInputResource) {
        if(question.getScored()) {
            if(!hasAssessorScore(formInputResources)) {
                FormInputResource assessorScore = new FormInputResource();
                assessorScore.setFormInputType(assessorScoreId);
                assessorScore.setGuidanceQuestion(null);
                assessorScore.setGuidanceAnswer(null);
                assessorScore.setDescription("QuestionViewModel score");
                assessorScore.setIncludedInApplicationSummary(false);
                assessorScore.setQuestion(questionResource.getId());
                assessorScore.setCompetition(formInputResource.getCompetition());
                assessorScore.setScope(FormInputScope.ASSESSMENT);
                assessorScore.setPriority(0);
                assessorScore.setWordCount(null);

                formInputService.save(assessorScore);
            }
        } else {
            if(hasAssessorScore(formInputResources)) {
                Long scoreId = formInputResources
                        .stream()
                        .filter(formInput -> formInput.getScope() == FormInputScope.ASSESSMENT &&
                            formInput.getFormInputType().equals(assessorScoreId))
                        .findFirst()
                        .get()
                        .getId();
                formInputService.delete(scoreId);
            }
        }

    }

    private Boolean hasAssessorScore(List<FormInputResource> formInputResources) {
        return checkFormInputsForType(formInputResources, assessorScoreId);
    }

    private Boolean checkFormInputsForType(List<FormInputResource> formInputResources, Long typeId) {
        return formInputResources.stream().anyMatch(formInput -> formInput.getFormInputType().equals(typeId));
    }
}
