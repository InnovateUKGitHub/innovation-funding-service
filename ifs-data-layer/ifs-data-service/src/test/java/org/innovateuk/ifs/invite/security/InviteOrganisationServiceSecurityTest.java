package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.InviteOrganisationService;
import org.innovateuk.ifs.invite.transactional.InviteOrganisationServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in {@link InviteOrganisationService} interact with Spring Security
 */
public class InviteOrganisationServiceSecurityTest extends BaseServiceSecurityTest<InviteOrganisationService> {

    ApplicationInvitePermissionRules invitePermissionRules;
    InviteOrganisationPermissionRules inviteOrganisationPermissionRules;

    @Before
    public void lookupPermissionRules() {
        invitePermissionRules = getMockPermissionRulesBean(ApplicationInvitePermissionRules.class);
        inviteOrganisationPermissionRules = getMockPermissionRulesBean(InviteOrganisationPermissionRules.class);
    }

    @Test
    public void getById() {
        when(classUnderTestMock.getById(1L))
                .thenReturn(serviceSuccess(newInviteOrganisationResource().build()));

        assertAccessDenied(
                () -> classUnderTest.getById(1L),
                () -> {
                    verify(inviteOrganisationPermissionRules).consortiumCanViewAnInviteOrganisation(isA(InviteOrganisationResource.class), isA(UserResource.class));
                    verify(inviteOrganisationPermissionRules).systemRegistrarCanViewOrganisationInviteToTheApplication(isA(InviteOrganisationResource.class),isA(UserResource.class));
                });
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() {
        when(classUnderTestMock.getByOrganisationIdWithInvitesForApplication(1L, 2L))
                .thenReturn(serviceSuccess(newInviteOrganisationResource().build()));

        assertAccessDenied(
                () -> classUnderTest.getByOrganisationIdWithInvitesForApplication(1L, 2L),
                () -> verify(inviteOrganisationPermissionRules).consortiumCanViewAnInviteOrganisation(isA(InviteOrganisationResource.class), isA(UserResource.class)));
    }

    @Test
    public void save() {
        assertAccessDenied(
                () -> classUnderTest.save(newInviteOrganisationResource().build()),
                () -> verify(inviteOrganisationPermissionRules).leadApplicantCanSaveInviteAnOrganisationToTheApplication(isA(InviteOrganisationResource.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends InviteOrganisationService> getClassUnderTest() {
        return InviteOrganisationServiceImpl.class;
    }
}
