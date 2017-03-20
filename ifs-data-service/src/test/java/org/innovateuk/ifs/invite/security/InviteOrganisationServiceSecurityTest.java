package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.transactional.InviteOrganisationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;


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
    public void findOne() {
        final long inviteId = 1L;
        assertAccessDenied(
                () -> classUnderTest.findOne(inviteId),
                () -> verify(inviteOrganisationPermissionRules).consortiumCanViewAnInviteOrganisation(isA(InviteOrganisationResource.class), isA(UserResource.class)));
    }

    @Test
    public void getByIdWithInvitesForApplication() {
        assertAccessDenied(
                () -> classUnderTest.getByIdWithInvitesForApplication(1L, 2L),
                () -> verify(inviteOrganisationPermissionRules).consortiumCanViewAnInviteOrganisation(isA(InviteOrganisationResource.class), isA(UserResource.class)));

    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() {
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
    protected Class<TestInviteOrganisationService> getClassUnderTest() {
        return TestInviteOrganisationService.class;
    }

    public static class TestInviteOrganisationService implements InviteOrganisationService {

        @Override
        public ServiceResult<InviteOrganisationResource> findOne(Long id) {
            return serviceSuccess(newInviteOrganisationResource().build());
        }

        @Override
        public ServiceResult<InviteOrganisationResource> getByIdWithInvitesForApplication(long inviteOrganisationId, long applicationId) {
            return serviceSuccess(newInviteOrganisationResource().build());
        }

        @Override
        public ServiceResult<InviteOrganisationResource> getByOrganisationIdWithInvitesForApplication(long organisationId, long applicationId) {
            return serviceSuccess(newInviteOrganisationResource().build());
        }

        @Override
        public ServiceResult<InviteOrganisationResource> save(InviteOrganisationResource inviteOrganisationResource) {
            return null;
        }
    }
}
