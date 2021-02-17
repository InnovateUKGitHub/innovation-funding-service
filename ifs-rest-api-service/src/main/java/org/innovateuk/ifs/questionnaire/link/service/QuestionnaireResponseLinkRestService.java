package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.string.resource.StringResource;

public interface QuestionnaireResponseLinkRestService {

    RestResult<StringResource> getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(long questionnaireId,
                                                                                               long applicationId,
                                                                                               long organisationId);
}
