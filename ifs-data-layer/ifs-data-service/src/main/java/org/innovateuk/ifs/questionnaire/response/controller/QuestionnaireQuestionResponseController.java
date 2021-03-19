package org.innovateuk.ifs.questionnaire.response.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireQuestionResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaire-question-response")
public class QuestionnaireQuestionResponseController extends AbstractCrudController<QuestionnaireQuestionResponseResource, Long> {

    @Autowired
    private QuestionnaireQuestionResponseService questionnaireQuestionResponseService;

    @GetMapping("/question/{questionId}/response/{responseId}")
    public RestResult<QuestionnaireQuestionResponseResource> findByQuestionnaireQuestionIdAndQuestionnaireResponseId(@PathVariable long questionId,
                                                                                                                     @PathVariable String responseId) {
        return questionnaireQuestionResponseService.findByQuestionnaireQuestionIdAndQuestionnaireResponseId(questionId, responseId).toGetResponse();
    }

    @Override
    protected IfsCrudService<QuestionnaireQuestionResponseResource, Long> crudService() {
        return questionnaireQuestionResponseService;
    }
}
