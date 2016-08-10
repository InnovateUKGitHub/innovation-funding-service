package com.worth.ifs.invite.security;


import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.user.resource.UserResource;

public class ProjectInvitePermissionRulesTest extends BasePermissionRulesTest<ProjectInvitePermissionRules> {

    private UserResource initiatingInvitePartner;
    private UserResource invitedPartner;
    private ProjectInvite invite;
    private InviteProjectResource inviteProjectResource;


    @Override
    protected ProjectInvitePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectInvitePermissionRules();
    }

}
