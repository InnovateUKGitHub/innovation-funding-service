package org.innovateuk.ifs.project.projectdetails.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.core.security.ProjectPermissionRules;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectDetailsService interact with Spring Security
 */
public class ProjectDetailsServiceSecurityTest extends BaseServiceSecurityTest<ProjectDetailsService> {

    private ProjectPermissionRules projectPermissionRules;
    private ProjectDetailsPermissionRules projectDetailsPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectPermissionRules = getMockPermissionRulesBean(ProjectPermissionRules.class);
        projectDetailsPermissionRules = getMockPermissionRulesBean(ProjectDetailsPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void updateProjectAddress() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(456L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateProjectAddress(123L, 456L, newAddressResource().build()), () -> {
            verify(projectDetailsPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Test
    public void updateFinanceContact() {

        ProjectResource project = newProjectResource().build();
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.updateFinanceContact(composite, 789L), () -> {
            verify(projectDetailsPermissionRules).partnersCanUpdateTheirOwnOrganisationsFinanceContacts(composite, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Test
    public void updatePartnerProjectLocation() {

        Long projectId = 1L;
        Long organisationId = 2L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.updatePartnerProjectLocation(projectOrganisationCompositeId, "TW14 9QG"), () -> {
            verify(projectDetailsPermissionRules).partnersCanUpdateProjectLocationForTheirOwnOrganisation(projectOrganisationCompositeId, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Test
    public void getProjectManager() {
        ProjectResource project = newProjectResource().build();

        when(classUnderTestMock.getProjectManager(123L))
                .thenReturn(serviceSuccess(
                        newProjectUserResource()
                                .withProject(123L)
                                .withRoleName("project-manager")
                                .build()
                ));

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(
                () -> classUnderTest.getProjectManager(123L),
                () -> {
                    verify(projectPermissionRules, times(1))
                            .partnersOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1))
                            .internalUsersCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1))
                            .monitoringOfficerOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1))
                            .stakeholdersCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(projectPermissionRules);
                }
        );
    }

    @Test
    public void setProjectManager() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.setProjectManager(123L, 456L), () -> {
            verify(projectDetailsPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectDetailsPermissionRules);
        });
    }

    @Override
    protected Class<? extends ProjectDetailsService> getClassUnderTest() {
        return ProjectDetailsServiceImpl.class;
    }
}

