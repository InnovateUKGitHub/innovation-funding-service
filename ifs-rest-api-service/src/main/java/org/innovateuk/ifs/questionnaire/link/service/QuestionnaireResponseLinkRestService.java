package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireLinkResource;
import org.innovateuk.ifs.string.resource.StringResource;

public interface QuestionnaireResponseLinkRestService {

    RestResult<QuestionnaireLinkResource> getQuestionnaireLink(String responseId);

    RestResult<StringResource> getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(long questionnaireId,
                                                                                               long applicationId,
                                                                                               long organisationId);

    RestResult<StringResource> getResponseIdByProjectIdAndQuestionnaireIdAndOrganisationId(long projectId,
                                                                                           long questionnaireId,
                                                                                           long organisationId);




}
