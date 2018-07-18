package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InviteServiceImpl implements InviteService {

    @Autowired
    private InviteRestService inviteRestService;

    @Override
    public List<ApplicationInviteResource> getPendingInvitationsByApplicationId(Long applicationId) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(applicationId);

        if(pendingAssignableUsersResult.isSuccess()){
            return pendingAssignableUsersResult.getSuccess()
                    .stream()
                    .flatMap(item -> item.getInviteResources().stream())
                    .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(0);
        }
    }
}
