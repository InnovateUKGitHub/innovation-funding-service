package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("permitAll")
public interface QuestionnaireTextOutcomeService extends IfsCrudService<QuestionnaireTextOutcomeResource, Long> {

}
