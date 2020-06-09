package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationServiceImpl;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PartnerOrganisationServiceSecurityTest extends BaseServiceSecurityTest<PartnerOrganisationService> {

    private PartnerOrganisationPermissionRules partnerOrganisationPermissionRules;
    private static List<PartnerOrganisationResource> partnerOrganisations;

    @Before
    public void lookupPermissionRules() {
        partnerOrganisationPermissionRules = getMockPermissionRulesBean(PartnerOrganisationPermissionRules.class);
        partnerOrganisations = newPartnerOrganisationResource().withProject(123L).build(3);
    }

    @Test
    public void getProjectPartnerOrganisationsIsNotOpenToAll() {
        when(classUnderTestMock.getProjectPartnerOrganisations(123L))
                .thenReturn(serviceSuccess(partnerOrganisations));

        assertPostFilter(classUnderTest.getProjectPartnerOrganisations(123L).getSuccess(), () -> {
            verify(partnerOrganisationPermissionRules, times(3))
                    .partnersOnProjectCanView(isA(PartnerOrganisationResource.class), isA(UserResource.class));
            verify(partnerOrganisationPermissionRules, times(3))
                    .internalUsersCanView(isA(PartnerOrganisationResource.class), isA(UserResource.class));
            verify(partnerOrganisationPermissionRules, times(3))
                    .monitoringOfficersUsersCanView(isA(PartnerOrganisationResource.class), isA(UserResource.class));
            verify(partnerOrganisationPermissionRules, times(3))
                    .stakeholdersCanViewProjects(isA(PartnerOrganisationResource.class), isA(UserResource.class));
            verify(partnerOrganisationPermissionRules, times(3))
                    .competitionFinanceUsersCanViewProjects(isA(PartnerOrganisationResource.class), isA(UserResource.class));

            verifyNoMoreInteractions(partnerOrganisationPermissionRules);
        });
    }

    @Test
    public void compAdminCanSeeAllPartnerOrganisationsForAnyProject() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());

        when(classUnderTestMock.getProjectPartnerOrganisations(123L))
                .thenReturn(serviceSuccess(partnerOrganisations));

        ServiceResult<List<PartnerOrganisationResource>> result =
                classUnderTest.getProjectPartnerOrganisations(123L);

        verify(partnerOrganisationPermissionRules, times(3))
                .partnersOnProjectCanView(isA(PartnerOrganisationResource.class), isA(UserResource.class));
        verify(partnerOrganisationPermissionRules, times(3))
                .internalUsersCanView(isA(PartnerOrganisationResource.class), isA(UserResource.class));
        verify(partnerOrganisationPermissionRules, times(3))
                .monitoringOfficersUsersCanView(isA(PartnerOrganisationResource.class), isA(UserResource.class));
        verify(partnerOrganisationPermissionRules, times(3))
                .stakeholdersCanViewProjects(isA(PartnerOrganisationResource.class), isA(UserResource.class));
        verify(partnerOrganisationPermissionRules, times(3))
                .competitionFinanceUsersCanViewProjects(isA(PartnerOrganisationResource.class), isA(UserResource.class));

        verifyNoMoreInteractions(partnerOrganisationPermissionRules);

        assertTrue(result.isSuccess());
    }

    @Override
    protected Class<? extends PartnerOrganisationService> getClassUnderTest() {
        return PartnerOrganisationServiceImpl.class;
    }
}

