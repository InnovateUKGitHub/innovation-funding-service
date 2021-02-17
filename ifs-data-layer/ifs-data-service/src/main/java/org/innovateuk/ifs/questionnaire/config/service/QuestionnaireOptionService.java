package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("permitAll")
public interface QuestionnaireOptionService extends IfsCrudService<QuestionnaireOptionResource, Long> {

}
