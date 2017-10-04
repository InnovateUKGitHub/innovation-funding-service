package org.innovateuk.ifs.project.projectdetails.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in ProjectDetailsService interact with Spring Security
 */
public class ProjectDetailsServiceSecurityTest extends BaseServiceSecurityTest<ProjectDetailsService> {

    private ProjectDetailsPermissionRules projectDetailsPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectDetailsPermissionRules = getMockPermissionRulesBean(ProjectDetailsPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testUpdateProjectStartDate() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateProjectStartDate(123L, LocalDate.now()), () -> {
            verify(projectDetailsPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Test
    public void testUpdateProjectAddress() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(456L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateProjectAddress(123L, 456L, OrganisationAddressType.ADD_NEW, newAddressResource().build()), () -> {
            verify(projectDetailsPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Test
    public void testUpdateFinanceContact() {

        ProjectResource project = newProjectResource().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateFinanceContact(composite, 789L), () -> {
            verify(projectDetailsPermissionRules).partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Test
    public void testSetProjectManager() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.setProjectManager(123L, 456L), () -> {
            verify(projectDetailsPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Override
    protected Class<TestProjectService> getClassUnderTest() {
        return TestProjectService.class;
    }

    public static class TestProjectService implements ProjectDetailsService {

        @Override
        public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateProjectAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource projectAddress) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
            return null;
        }

        @Override
        public ServiceResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> inviteProjectManager(Long projectId, InviteProjectResource inviteResource) {
            return null;
        }
    }
}

