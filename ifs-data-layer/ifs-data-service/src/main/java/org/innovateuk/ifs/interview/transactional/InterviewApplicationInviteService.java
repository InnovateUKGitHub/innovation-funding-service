package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service for inviting applicants to interview panels.
 */
public interface InterviewApplicationInviteService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATION_EMAIL_TEMPLATE",
            description = "The Competition Admin user and Project Finance users can view template for inviting applicants")
    ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATION_SEND_INVITE",
            description = "The Competition Admin user and Project Finance users can send invites to applicants")
    ServiceResult<Void> sendInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATION_SENT_INVITE",
            description = "The Competition Admin user and Project Finance users can view sent invites to applicants")
    ServiceResult<InterviewApplicationSentInviteResource> getSentInvite(long applicationId);


    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATION_RESEND_INVITE",
            description = "The Competition Admin user and Project Finance users can resend invites to applicants")
    ServiceResult<Void> resendInvite(long applicationId, AssessorInviteSendResource assessorInviteSendResource);
}