package com.worth.ifs.invite.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.invite.transactional.InviteService;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.invite.builder.InviteBuilder.newInvite;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// TODO
public class InviteServiceSecurityTest extends BaseServiceSecurityTest<InviteService> {

    InvitePermissionRules invitePermissionRules;
    InviteOrganisationPermissionRules inviteOrganisationPermissionRules;

    @Before
    public void lookupPermissionRules() {
        invitePermissionRules = getMockPermissionRulesBean(InvitePermissionRules.class);
        inviteOrganisationPermissionRules= getMockPermissionRulesBean(InviteOrganisationPermissionRules.class);
    }

    @Test
    public void testInviteCollaborators() {
        final int nInvites = 2;
        final String baseUrl = "test";
        final List<Invite> invites = newInvite().build(nInvites);
        service.inviteCollaborators(baseUrl, invites);
        verify(invitePermissionRules, times(nInvites)).leadApplicantCanInviteToTheApplication(any(Invite.class), any(UserResource.class));
        verify(invitePermissionRules, times(nInvites)).collaboratorCanInviteToApplicantForTheirOrganisation(any(Invite.class), any(UserResource.class));
    }

    @Test
    public void testInviteCollaboratorToApp() {
        final String baseUrl = "test";
        final Invite invite = newInvite().build();
        assertAccessDenied(
                () -> service.inviteCollaboratorToApplication(baseUrl, invite),
                () -> {
                    verify(invitePermissionRules).leadApplicantCanInviteToTheApplication(eq(invite), any(UserResource.class));
                    verify(invitePermissionRules).collaboratorCanInviteToApplicantForTheirOrganisation(eq(invite), any(UserResource.class));
                });
    }

    @Test
    public void testCreateApplicationInvites() {
        final InviteOrganisationResource inviteOrganisation = newInviteOrganisationResource().build();
        assertAccessDenied(
                () -> service.createApplicationInvites(inviteOrganisation),
                () -> {
                    verify(inviteOrganisationPermissionRules).leadApplicantCanInviteAnOrganisationToTheApplication(eq(inviteOrganisation), any(UserResource.class));
                });
    }


    @Override
    protected Class<? extends InviteService> getServiceClass() {
        return TestInviteService.class;
    }

    public static class TestInviteService implements InviteService {

        @Override
        public List<ServiceResult<Notification>> inviteCollaborators(String baseUrl, List<Invite> invites) {
            return new ArrayList<>();
        }

        @Override
        public ServiceResult<Notification> inviteCollaboratorToApplication(String baseUrl, Invite invite) {
            return null;
        }

        @Override
        public ServiceResult<Invite> findOne(Long id) {
            return null;
        }

        @Override
        public ServiceResult<InviteResultsResource> createApplicationInvites(InviteOrganisationResource inviteOrganisationResource) {
            return null;
        }

        @Override
        public ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash) {
            return null;
        }

        @Override
        public ServiceResult<Set<InviteOrganisationResource>> getInvitesByApplication(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<InviteResultsResource> saveInvites(List<InviteResource> inviteResources) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptInvite(String inviteHash, Long userId) {
            return null;
        }

        @Override
        public ServiceResult<InviteResource> getInviteByHash(String hash) {
            return null;
        }

        @Override
        public ServiceResult<Void> checkUserExistingByInviteHash(@P("hash") String hash) {
            return null;
        }
    }
}
