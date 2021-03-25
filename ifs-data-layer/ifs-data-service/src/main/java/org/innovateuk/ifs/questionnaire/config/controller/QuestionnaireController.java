package org.innovateuk.ifs.questionnaire.config.controller;

import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireController extends AbstractCrudController<QuestionnaireResource, Long> {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Override
    protected IfsCrudService<QuestionnaireResource, Long> crudService() {
        return questionnaireService;
    }
}
