package com.worth.ifs.invite.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.transactional.InviteOrganisationService;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.invite.security.InviteOrganisationServiceSecurityTest.TestInviteOrganisationService.ARRAY_SIZE_FOR_POST_FILTER_TESTS;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
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
    public void testFindAll() {
        final ServiceResult<Iterable<InviteOrganisationResource>> all = classUnderTest.findAll();
        assertFalse(all.getSuccessObject().iterator().hasNext());
        verify(inviteOrganisationPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).leadApplicantCanViewOrganisationInviteToTheApplication(isA(InviteOrganisationResource.class), isA(UserResource.class));
        verify(inviteOrganisationPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).collaboratorCanViewOrganisationInviteToTheApplication(isA(InviteOrganisationResource.class), isA(UserResource.class));
    }


    @Test
    public void testFindOne() {
        final long inviteId = 1L;
        assertAccessDenied(
                () -> classUnderTest.findOne(inviteId),
                () -> {
                    verify(inviteOrganisationPermissionRules).collaboratorCanViewOrganisationInviteToTheApplication(isA(InviteOrganisationResource.class), isA(UserResource.class));
                    verify(inviteOrganisationPermissionRules).leadApplicantCanViewOrganisationInviteToTheApplication(isA(InviteOrganisationResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void testSave() {
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

        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;


        @Override
        public ServiceResult<InviteOrganisationResource> findOne(Long id) {
            return serviceSuccess(newInviteOrganisationResource().build());
        }

        @Override
        public ServiceResult<Iterable<InviteOrganisationResource>> findAll() {
            return serviceSuccess(newInviteOrganisationResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<InviteOrganisationResource> save(InviteOrganisationResource inviteOrganisationResource) {
            return null;
        }
    }
}
