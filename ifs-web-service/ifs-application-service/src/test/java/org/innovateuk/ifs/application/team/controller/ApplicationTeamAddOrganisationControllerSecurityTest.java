package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.overview.controller.BaseApplicationControllerSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.application.team.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.team.security.ApplicationPermissionRules;
import org.innovateuk.ifs.application.team.security.OrganisationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class ApplicationTeamAddOrganisationControllerSecurityTest extends BaseApplicationControllerSecurityTest<ApplicationTeamAddOrganisationController> {


    private ApplicationLookupStrategy applicationLookupStrategies;

    @Override
    protected Class<? extends ApplicationTeamAddOrganisationController> getClassUnderTest() {
        return ApplicationTeamAddOrganisationController.class;
    }

    @Before
    public void lookupPermissionRules(){
        applicationLookupStrategies = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }


    @Test
    public void testGetAddOrganisation() {
        when(applicationLookupStrategies.getApplicationCompositeId(123L)).thenReturn(ApplicationCompositeId.id(123L));
        assertSecured(() -> classUnderTest.getAddOrganisation(null, 123L, null, null),
                (OrganisationPermissionRules permissionRules) -> permissionRules.viewAddOrganisationPage(eq(ApplicationCompositeId.id(123L)), isA(UserResource.class)),
                OrganisationPermissionRules.class);
    }

    @Test
    public void testSubmitAddOrganisation() {
        when(applicationLookupStrategies.getApplicationCompositeId(123L)).thenReturn(ApplicationCompositeId.id(123L));
        assertSecured(() -> classUnderTest.submitAddOrganisation(null, 123L, null, null, null, null),
                (OrganisationPermissionRules permissionRules) -> permissionRules.addNewOrganisation(eq(ApplicationCompositeId.id(123L)), isA(UserResource.class)),
                OrganisationPermissionRules.class);
    }

    @Test
    public void testAddApplicant() {
        when(applicationLookupStrategies.getApplicationCompositeId(123L)).thenReturn(ApplicationCompositeId.id(123L));
        assertSecured(() -> classUnderTest.addApplicant(null, 123L, null, null),
                (ApplicationPermissionRules permissionRules) -> permissionRules.addApplicant(eq(ApplicationCompositeId.id(123L)), isA(UserResource.class)),
                ApplicationPermissionRules.class);
    }

    @Test
    public void testRemoveApplicant() {
        when(applicationLookupStrategies.getApplicationCompositeId(123L)).thenReturn(ApplicationCompositeId.id(123L));
        assertSecured(() -> classUnderTest.removeApplicant(null, 123L, null, null, null),
                (ApplicationPermissionRules permissionRules) -> permissionRules.removeApplicant(eq(ApplicationCompositeId.id(123L)), isA(UserResource.class)),
                ApplicationPermissionRules.class);
    }
}
