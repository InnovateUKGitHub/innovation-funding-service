package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;

public interface QuestionFileSetupCompetitionService {

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "UPLOAD_TEMPLATE_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to upload template files")
    ServiceResult<Void> uploadTemplateFile(String contentType, String contentLength, String originalFilename, long questionId,
                                           HttpServletRequest request);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "DELETE_TEMPLATE_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to delete template files")
    ServiceResult<Void> deleteTemplateFile(long questionId);

}
