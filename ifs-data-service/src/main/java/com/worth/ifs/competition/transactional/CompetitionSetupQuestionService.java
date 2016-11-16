package com.worth.ifs.competition.transactional;

import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for Question processing work
 */
public interface CompetitionSetupQuestionService {

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);

}
