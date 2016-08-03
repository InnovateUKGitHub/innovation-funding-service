package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.competitionsetup.model.Question;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetitionSetupQuestionServiceImpl implements CompetitionSetupQuestionService {

	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

	@Autowired
	private QuestionService questionService;

	@Autowired
	private FormInputService formInputService;

	@Override
	public void updateQuestion(Question question) {
		QuestionResource questionResource = questionService.getById(question.getId());
		List<FormInputResource> formInputResources = formInputService.findApplicationInputsByQuestion(question.getId());

		questionResource.setName(question.getTitle());
        questionResource.setDescription(question.getSubTitle());

        questionService.save(questionResource);

        FormInputResource formInputResource = formInputResources.stream().filter(formInput -> !formInput.getFormInputType().equals(4L)).findFirst().get();
        formInputResource.setGuidanceQuestion(question.getGuidanceTitle());
        formInputResource.setGuidanceAnswer(question.getGuidance());
        formInputResource.setWordCount(question.getMaxWords());

        formInputService.save(formInputResource);

        if(question.getAppendix()) {
            //check if is there or add
        } else {
            if(hasAppendix(formInputResources)) {
                //check if is there or delete
                Long appendixId = formInputResources.stream().filter(formInput -> formInput.getFormInputType().equals(4L)).findFirst().get().getId();
                formInputService.delete(appendixId);
            }
        }
	}

	private Boolean hasAppendix(List<FormInputResource> formInputResources) {
        return formInputResources.stream().anyMatch(formInput -> formInput.getFormInputType().equals(4L));
    }
}
