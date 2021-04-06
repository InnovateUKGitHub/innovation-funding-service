package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireLinkResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface QuestionnaireResponseLinkService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<UUID> getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(long applicationId, long organisationId, long questionnaireId);

    @PreAuthorize("hasPermission(#questionnaireResponseId, 'org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource', 'READ')")
    ServiceResult<QuestionnaireLinkResource> get(UUID questionnaireResponseId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<UUID> getResponseIdByProjectIdAndOrganisationIdAndQuestionnaireId(long projectId, long organisationId, long questionnaireId);
}
