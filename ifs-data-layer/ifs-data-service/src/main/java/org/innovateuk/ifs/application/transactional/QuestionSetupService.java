package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

/**
 * Transactional and secure service for Question processing work
 */
public interface QuestionSetupService {

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections incomplete")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> markInSetupAsComplete(Long questionId, Long competitionId);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections incomplete")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<SetupStatusResource> markInSetupAsInComplete(Long questionId, Long competitionId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the statuses")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Map<Long, Boolean>> getQuestionStatuses(Long competitionId, CompetitionSetupSection parentId);
}
