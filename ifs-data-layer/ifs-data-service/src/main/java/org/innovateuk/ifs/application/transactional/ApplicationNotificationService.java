package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;


/**
 * Security annotated interface for {@ApplicationNotificationServiceImpl}.
 */
public interface ApplicationNotificationService {
    @SecuredBySpring(value = "NOTIFY_APPLICANTS_OF_FEEDBACK",
                    description = "Comp admins and project finance users can notify applicants that their feedback is released")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<Void> notifyApplicantsByCompetition(Long competitionId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'APPLICATION_SUBMITTED_NOTIFICATION')")
    ServiceResult<Void> sendNotificationApplicationSubmitted(@P("applicationId") Long applicationId);

    @SecuredBySpring(value = "INFORM_APPLICATION_IS_INELIGIBLE",
            description = "Comp admins and project finance users can inform applicants that their application is ineligible")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> informIneligible(long applicationId,
                                         ApplicationIneligibleSendResource applicationIneligibleSendResource);
}
