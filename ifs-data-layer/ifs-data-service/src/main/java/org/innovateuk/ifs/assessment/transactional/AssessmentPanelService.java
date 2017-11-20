package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service for managing assessment panel status of {@link org.innovateuk.ifs.application.domain.Application}s
 */
public interface AssessmentPanelService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "ASSIGN_APPLICATIONS_TO_PANEL",
            description = "Comp admins and execs can assign applications to an assessment panel")
    ServiceResult<Void> assignApplicationToPanel(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "UNASSIGN_APPLICATIONS_FROM_PANEL",
            description = "Comp admins and execs can unassign applications from an assessment panel")
    ServiceResult<Void> unAssignApplicationFromPanel(long applicationId);

}
