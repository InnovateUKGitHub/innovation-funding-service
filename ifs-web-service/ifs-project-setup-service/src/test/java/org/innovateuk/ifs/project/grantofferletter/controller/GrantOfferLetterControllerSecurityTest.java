package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class GrantOfferLetterControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<GrantOfferLetterController> {

    private ProjectLookupStrategy projectLookupStrategy;
    private ProjectCompositeId projectCompositeId;


    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123l);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends GrantOfferLetterController> getClassUnderTest() {
        return GrantOfferLetterController.class;
    }

    @Test
    public void testPublicMethods() {
        assertSecured(() -> classUnderTest.confirmation(projectCompositeId .id(), null),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(projectCompositeId ), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.downloadAdditionalContractFile(projectCompositeId .id()),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(projectCompositeId ), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.downloadGeneratedGrantOfferLetterFile(projectCompositeId .id()),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(projectCompositeId ), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.submit(projectCompositeId .id(), null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(projectCompositeId ), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.uploadSignedGrantOfferLetterFile(projectCompositeId .id(), null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(projectCompositeId ), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.downloadGrantOfferLetterFile(projectCompositeId .id()),
                permissionRules -> permissionRules.leadPartnerAccess(eq(projectCompositeId ), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.deleteSignedGrantOfferLetterFile(projectCompositeId .id(), null, null, null, null, null),
                permissionRules -> permissionRules.leadPartnerAccess(eq(projectCompositeId ), isA(UserResource.class)));
    }
}
