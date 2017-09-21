package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AssessmentPanelInviteRestServiceImpl extends BaseRestService implements AssessmentPanelInviteRestService {

    private static final String assessmentPanelInviteRestUrl = "/assessmentpanelinvite";


    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "sendAllInvites", competitionId), assessorInviteSendResource, Void.class);
    }
}
