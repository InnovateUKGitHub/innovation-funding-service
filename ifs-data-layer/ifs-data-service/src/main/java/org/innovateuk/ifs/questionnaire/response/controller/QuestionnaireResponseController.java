package org.innovateuk.ifs.questionnaire.response.controller;

import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.innovateuk.ifs.questionnaire.response.service.QuestionnaireResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/questionnaire-response")
public class QuestionnaireResponseController extends AbstractCrudController<QuestionnaireResponseResource, UUID> {
    @Autowired
    private QuestionnaireResponseService questionnaireResponseService;

    @Override
    protected IfsCrudService<QuestionnaireResponseResource, UUID> crudService() {
        return questionnaireResponseService;
    }
}
