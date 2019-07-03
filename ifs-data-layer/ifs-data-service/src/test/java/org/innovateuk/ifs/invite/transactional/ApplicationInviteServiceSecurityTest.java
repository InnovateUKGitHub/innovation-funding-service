package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.security.ApplicationInvitePermissionRules;
import org.innovateuk.ifs.invite.security.InviteOrganisationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ApplicationInviteService interact with Spring Security
 */
public class ApplicationInviteServiceSecurityTest extends BaseServiceSecurityTest<ApplicationInviteService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    ApplicationInvitePermissionRules invitePermissionRules;
    InviteOrganisationPermissionRules inviteOrganisationPermissionRules;

    @Before
    public void lookupPermissionRules() {
        invitePermissionRules = getMockPermissionRulesBean(ApplicationInvitePermissionRules.class);
        inviteOrganisationPermissionRules = getMockPermissionRulesBean(InviteOrganisationPermissionRules.class);
    }

    @Test
    public void createApplicationInvites() {
        final InviteOrganisationResource inviteOrganisation = newInviteOrganisationResource().build();
        final long applicationId = 1L;
        assertAccessDenied(
                () -> classUnderTest.createApplicationInvites(inviteOrganisation, Optional.of(applicationId)),
                () -> {
                    verify(inviteOrganisationPermissionRules)
                            .leadApplicantCanCreateApplicationInvitesIfApplicationEditable(eq(inviteOrganisation),
                                    any(UserResource.class));
                });
    }

    @Test
    public void getInvitesByApplication() {
        long applicationId = 1L;

        when(classUnderTestMock.getInvitesByApplication(applicationId))
                .thenReturn(serviceSuccess(newInviteOrganisationResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        final ServiceResult<List<InviteOrganisationResource>> results = classUnderTest.getInvitesByApplication
                (applicationId);

        verify(inviteOrganisationPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanViewAnyInviteOrganisation(any(InviteOrganisationResource.class), any(UserResource.class));

        assertTrue(results.getSuccess().isEmpty());
    }

    @Test
    public void saveInvites() {
        int nInvites = 2;
        final List<ApplicationInviteResource> invites = newApplicationInviteResource().build(nInvites);

        classUnderTest.saveInvites(invites);
        verify(invitePermissionRules, times(nInvites))
                .collaboratorCanSaveInviteToApplicationForTheirOrganisation(any(ApplicationInviteResource.class), any
                        (UserResource.class));
        verify(invitePermissionRules, times(nInvites))
                .leadApplicantCanSaveInviteToTheApplication(any(ApplicationInviteResource.class), any(UserResource
                        .class));
    }

    @Test
    public void resendInvite() {
        final ApplicationInviteResource invite = newApplicationInviteResource().build();

        assertAccessDenied(
                () -> classUnderTest.resendInvite(invite),
                () -> {
                    verify(invitePermissionRules)
                            .collaboratorCanSaveInviteToApplicationForTheirOrganisation(any(ApplicationInviteResource.class), any
                                    (UserResource.class));
                    verify(invitePermissionRules)
                            .leadApplicantCanSaveInviteToTheApplication(any(ApplicationInviteResource.class), any(UserResource
                                    .class));
                });
    }

    @Override
    protected Class<? extends ApplicationInviteService> getClassUnderTest() {
        return ApplicationInviteServiceImpl.class;
    }
}
