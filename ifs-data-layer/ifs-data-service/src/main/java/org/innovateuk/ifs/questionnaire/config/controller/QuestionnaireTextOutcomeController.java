package org.innovateuk.ifs.questionnaire.config.controller;

import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaire-text-outcome")
public class QuestionnaireTextOutcomeController extends AbstractCrudController<QuestionnaireTextOutcomeResource, Long> {

    @Autowired
    private QuestionnaireTextOutcomeService questionnaireTextOutcomeService;

    @Override
    protected IfsCrudService<QuestionnaireTextOutcomeResource, Long> crudService() {
        return questionnaireTextOutcomeService;
    }
}
