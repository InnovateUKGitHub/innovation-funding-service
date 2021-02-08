package org.innovateuk.ifs.questionnaire.config.controller;

import org.innovateuk.ifs.crud.AbstractCrudController;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaire-option")
public class QuestionnaireOptionController extends AbstractCrudController<QuestionnaireOptionResource, Long> {
}
