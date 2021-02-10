package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.commons.rest.RestResult;

public interface QuestionnaireResponseLinkRestService {

    RestResult<Long> getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(long questionnaireId,
                                long applicationId,
                                long organisationId);
 //
}
