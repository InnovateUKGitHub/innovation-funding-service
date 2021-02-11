package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

/**
 * Transactional service for Question setup statuses
 */
public interface QuestionSetupService {

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections complete")
    @PreAuthorize("hasAnyAuthority('comp_admin')")
    ServiceResult<SetupStatusResource> markQuestionInSetupAsComplete(Long questionId, Long competitionId, CompetitionSetupSection parentSection);

    @SecuredBySpring(value = "UPDATE", description = "Only those with either comp admin or project finance roles can mark sections incomplete")
    @PreAuthorize("hasAnyAuthority('comp_admin')")
    ServiceResult<SetupStatusResource> markQuestionInSetupAsIncomplete(Long questionId, Long competitionId, CompetitionSetupSection parentSection);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the statuses")
    @PreAuthorize("hasAnyAuthority('comp_admin')")
    ServiceResult<Map<Long, Boolean>> getQuestionStatuses(Long competitionId, CompetitionSetupSection parentId);
}
