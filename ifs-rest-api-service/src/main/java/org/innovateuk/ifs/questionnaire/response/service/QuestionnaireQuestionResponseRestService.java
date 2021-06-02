package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.crud.IfsCrudRestService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;

public interface QuestionnaireQuestionResponseRestService extends IfsCrudRestService<QuestionnaireQuestionResponseResource, Long> {

    RestResult<QuestionnaireQuestionResponseResource> findByQuestionnaireQuestionIdAndQuestionnaireResponseId(long questionnaireQuestionId, String questionnaireResponseId);
}
