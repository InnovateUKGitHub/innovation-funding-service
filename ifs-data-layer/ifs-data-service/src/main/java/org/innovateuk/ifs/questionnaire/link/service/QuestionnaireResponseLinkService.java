package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface QuestionnaireResponseLinkService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<UUID> getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(long applicationId, long organisationId, long questionnaireId);
}
