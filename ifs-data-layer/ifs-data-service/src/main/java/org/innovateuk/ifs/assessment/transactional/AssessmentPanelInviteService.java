package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.AssessmentPanelInvite}s.
 */
public interface AssessmentPanelInviteService {

    @SecuredBySpring(value = "GET_ALL_CREATED_INVITES",
            description = "Competition Admins and Project Finance users can get all invites that have been created for a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    @SecuredBySpring(value = "SEND_ALL_INVITES",
            description = "The Competition Admins and Project Finance users can send all competition invites")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInvitesToSendResource);
}
