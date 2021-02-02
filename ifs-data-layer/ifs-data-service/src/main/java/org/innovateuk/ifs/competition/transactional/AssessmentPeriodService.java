package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AssessmentPeriodService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to create the milestone for the given competitions")
    ServiceResult<List<MilestoneResource>> createAssessmentPeriodMilestones(Long id);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = MilestoneResource.class,
            description = "Only Comp Admins and project finance users are able to save single milestone for the given competitions")
    ServiceResult<Void> updateAssessmentPeriodMilestones(List<MilestoneResource> milestones);
}
