package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("permitAll")
public interface QuestionnaireService extends IfsCrudService<QuestionnaireResource, Long> {

}
