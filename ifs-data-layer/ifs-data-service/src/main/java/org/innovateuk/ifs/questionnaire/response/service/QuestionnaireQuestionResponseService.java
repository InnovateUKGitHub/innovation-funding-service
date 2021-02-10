package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;

public interface QuestionnaireQuestionResponseService extends IfsCrudService<QuestionnaireQuestionResponseResource, Long> {

    ServiceResult<QuestionnaireQuestionResponseResource> findByQuestionnaireQuestionIdAndQuestionnaireResponseId(long questionId, long responseId);
}
