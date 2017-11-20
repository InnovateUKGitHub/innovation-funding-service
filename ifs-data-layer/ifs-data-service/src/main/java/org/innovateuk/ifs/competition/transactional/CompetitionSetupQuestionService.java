package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.*;
import org.innovateuk.ifs.commons.service.*;
import org.innovateuk.ifs.competition.resource.*;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Question processing work
 */
public interface CompetitionSetupQuestionService {

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "GET_COMPETITION_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to view the competition setup details for a question")
    ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "SAVE_COMPETITION_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to edit the competition setup details for a question")
    ServiceResult<CompetitionSetupQuestionResource> update(CompetitionSetupQuestionResource competitionSetupQuestionResource);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "CREATE_COMPETITION_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to create competition assessed questions")
    ServiceResult<CompetitionSetupQuestionResource> createByCompetitionId(Long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "DELETE_COMPETITION_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to delete competition assessed questions")
    ServiceResult<Void> delete(Long questionId);
}
