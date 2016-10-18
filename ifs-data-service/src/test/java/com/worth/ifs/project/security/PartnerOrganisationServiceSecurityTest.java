package com.worth.ifs.project.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import com.worth.ifs.project.transactional.PartnerOrganisationService;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PartnerOrganisationServiceSecurityTest extends BaseServiceSecurityTest<PartnerOrganisationService> {

    private PartnerOrganisationPermissionRules partnerOrganisationPermissionRules;
    private static List<PartnerOrganisationResource> partnerOrganisations = newPartnerOrganisationResource().withProject(123L).build(3);;

    @Before
    public void lookupPermissionRules() {
        partnerOrganisationPermissionRules = getMockPermissionRulesBean(PartnerOrganisationPermissionRules.class);
    }

    @Test
    public void testGetProjectPartnerOrganisationsIsNotOpenToAll(){
        assertPostFilter(classUnderTest.getProjectPartnerOrganisations(123L).getSuccessObject(), () -> {
            verify(partnerOrganisationPermissionRules, times(3)).partnersOnProjectCanView(isA(PartnerOrganisationResource.class), isA(UserResource.class));
            verify(partnerOrganisationPermissionRules, times(3)).compAdminsCanViewProjects(isA(PartnerOrganisationResource.class), isA(UserResource.class));
            verify(partnerOrganisationPermissionRules, times(3)).projectFinanceUsersCanViewProjects(isA(PartnerOrganisationResource.class), isA(UserResource.class));
            verifyNoMoreInteractions(partnerOrganisationPermissionRules);
        });
    }

    @Test
    public void testCompAdminCanSeeAllPartnerOrganisationsForAnyProject(){
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());
        ServiceResult<List<PartnerOrganisationResource>> result = classUnderTest.getProjectPartnerOrganisations(123L);
        assertTrue(result.isSuccess());
        //assertEquals(3, result.getSuccessObject().size());
    }

    @Override
    protected Class<TestPartnerOrganisationService> getClassUnderTest() {
        return TestPartnerOrganisationService.class;
    }

    static class TestPartnerOrganisationService implements PartnerOrganisationService {
        @Override
        public ServiceResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
            return serviceSuccess(partnerOrganisations);
        }
    }
}

