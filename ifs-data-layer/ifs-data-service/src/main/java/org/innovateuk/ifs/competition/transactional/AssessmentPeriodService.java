package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of AssessmentPeriod
 */
public interface AssessmentPeriodService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value="READ", securedType= AssessmentPeriodResource.class,
            description = "Only Comp Admins are able to get the assessment periods for the given competitions")
    ServiceResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(Long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value="UPDATE", securedType= AssessmentPeriodResource.class,
            description = "Only Comp Admins are able to create the assessment periods for the given competitions")
    ServiceResult<AssessmentPeriodResource> create(Long competitionId, Integer index);
}
