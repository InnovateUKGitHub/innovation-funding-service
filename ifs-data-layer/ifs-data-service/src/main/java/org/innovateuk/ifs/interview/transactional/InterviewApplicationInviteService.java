package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service for managing {@link InterviewInvite}s
 */
public interface InterviewApplicationInviteService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATIONS",
            description = "The Competition Admin user and Project Finance users can view template for inviting applicants")
    ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATIONS",
            description = "The Competition Admin user and Project Finance users can send invites to applicants")
    ServiceResult<Void> sendInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

}