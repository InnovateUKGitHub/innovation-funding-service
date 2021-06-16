package org.innovateuk.ifs.assessment.period.transactional;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.crud.IfsCrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of AssessmentPeriod
 */
public interface AssessmentPeriodService extends IfsCrudService<AssessmentPeriodResource, Long> {
    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType= AssessmentPeriodResource.class,
            description = "Only comp admins can perform actions on assessment periods")
    ServiceResult<AssessmentPeriodResource> get(Long aLong);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType= AssessmentPeriodResource.class,
            description = "Only comp admins can perform actions on assessment periods")
    ServiceResult<List<AssessmentPeriodResource>> get(List<Long> longs);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType= AssessmentPeriodResource.class,
            description = "Only comp admins can perform actions on assessment periods")
    ServiceResult<AssessmentPeriodResource> update(Long aLong, AssessmentPeriodResource assessmentPeriodResource);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType= AssessmentPeriodResource.class,
            description = "Only comp admins can perform actions on assessment periods")
    ServiceResult<Void> delete(Long aLong);

    @Override
    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType= AssessmentPeriodResource.class,
            description = "Only comp admins can perform actions on assessment periods")
    ServiceResult<AssessmentPeriodResource> create(AssessmentPeriodResource assessmentPeriodResource);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'READ')")
    ServiceResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(long competitionId);

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType= AssessmentPeriodResource.class,
            description = "Only Comp Admins are able to get the assessment periods for the given competitions")
    ServiceResult<PageResource<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(long competitionId, Pageable page);
}
