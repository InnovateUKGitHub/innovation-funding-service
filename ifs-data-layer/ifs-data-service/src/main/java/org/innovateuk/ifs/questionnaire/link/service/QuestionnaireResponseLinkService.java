package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

public interface QuestionnaireResponseLinkService {

    ServiceResult<Long> getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(long applicationId, long organisationId, long questionnaireId);
}
