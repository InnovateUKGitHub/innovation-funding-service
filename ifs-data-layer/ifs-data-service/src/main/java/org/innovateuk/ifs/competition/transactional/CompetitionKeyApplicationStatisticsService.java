package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionKeyApplicationStatisticsService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "READ", securedType = CompetitionOpenKeyApplicationStatisticsResource.class,
            description = "Comp admins, project finance, innovation leads and stakeholders can see competition statistics")
    ServiceResult<CompetitionOpenKeyApplicationStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "READ", securedType = CompetitionClosedKeyApplicationStatisticsResource.class,
            description = "Comp admins, project finance, innovation leads and stakeholders can see competition statistics")
    ServiceResult<CompetitionClosedKeyApplicationStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "READ", securedType = CompetitionFundedKeyApplicationStatisticsResource.class,
            description = "Comp admins, project finance, innovation leads and stakeholders can see competition statistics")
    ServiceResult<CompetitionFundedKeyApplicationStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId);
}
