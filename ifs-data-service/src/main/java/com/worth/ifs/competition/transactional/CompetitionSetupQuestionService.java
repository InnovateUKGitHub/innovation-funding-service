package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.security.*;
import com.worth.ifs.commons.service.*;
import com.worth.ifs.competition.resource.*;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Question processing work
 */
public interface CompetitionSetupQuestionService {

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value = "GET_COMPETITION_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to view the competition setup details for a question")
    ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    @SecuredBySpring(value = "SAVE_COMPETITION_SETUP_QUESTION", securedType = CompetitionSetupQuestionResource.class, description = "Comp Admins and project finance users should be able to edit the competition setup details for a question")
    ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);

}
