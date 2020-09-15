package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.security.ApplicationInvitePermissionRules;
import org.innovateuk.ifs.invite.security.InviteOrganisationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.transactional.ApplicationServiceSecurityTest.verifyApplicationRead;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testing how the secured methods in ApplicationInviteService interact with Spring Security
 */
public class ApplicationInviteServiceSecurityTest extends BaseServiceSecurityTest<ApplicationInviteService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    ApplicationInvitePermissionRules invitePermissionRules;
    InviteOrganisationPermissionRules inviteOrganisationPermissionRules;
    ApplicationPermissionRules applicationPermissionRules;
    ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        invitePermissionRules = getMockPermissionRulesBean(ApplicationInvitePermissionRules.class);
        inviteOrganisationPermissionRules = getMockPermissionRulesBean(InviteOrganisationPermissionRules.class);
        applicationPermissionRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
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
        verifyApplicationRead(applicationLookupStrategy, applicationPermissionRules,
                (applicationId) -> classUnderTest.getInvitesByApplication(applicationId));
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
