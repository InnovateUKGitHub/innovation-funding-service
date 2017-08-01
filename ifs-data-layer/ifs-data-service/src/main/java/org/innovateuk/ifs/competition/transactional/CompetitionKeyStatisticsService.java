package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionKeyStatisticsService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionReadyToOpenKeyStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionOpenKeyStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionClosedKeyStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionInAssessmentKeyStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionFundedKeyStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId);
}
