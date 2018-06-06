package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionKeyAssessmentStatisticsService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionReadyToOpenKeyAssessmentStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionReadyToOpenKeyAssessmentStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionOpenKeyAssessmentStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionOpenKeyAssessmentStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionClosedKeyAssessmentStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionClosedKeyAssessmentStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'innovation_lead')")
    @SecuredBySpring(value = "READ", securedType = CompetitionInAssessmentKeyAssessmentStatisticsResource.class,
            description = "Comp admins and execs can see competition statistics")
    ServiceResult<CompetitionInAssessmentKeyAssessmentStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId);
}
