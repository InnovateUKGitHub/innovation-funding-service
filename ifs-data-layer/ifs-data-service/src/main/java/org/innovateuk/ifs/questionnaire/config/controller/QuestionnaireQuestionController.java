package org.innovateuk.ifs.questionnaire.config.controller;

import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaire-question")
public class QuestionnaireQuestionController extends AbstractCrudController<QuestionnaireQuestionResource, Long> {

    @Autowired
    private QuestionnaireQuestionService questionnaireQuestionService;

    @Override
    protected IfsCrudService<QuestionnaireQuestionResource, Long> crudService() {
        return questionnaireQuestionService;
    }
}
