package com.worth.ifs.project.grantofferletter.controller;

import com.worth.ifs.project.BaseProjectSetupControllerSecurityTest;
import com.worth.ifs.project.ProjectSetupSectionsPermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectGrantOfferLetterControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectGrantOfferLetterController> {

    @Override
    protected Class<? extends ProjectGrantOfferLetterController> getClassUnderTest() {
        return ProjectGrantOfferLetterController.class;
    }

    @Test
    public void testPublicMethods() {
        assertSecured(() -> classUnderTest.confirmation(123L, null));
        assertSecured(() -> classUnderTest.downloadAdditionalContrcatFile(123L));
        assertSecured(() -> classUnderTest.downloadGeneratedGrantOfferLetterFile(123L));
        assertSecured(() -> classUnderTest.downloadGrantOfferLetterFile(123L));
        assertSecured(() -> classUnderTest.submit(123L, null, null, null, null, null));
        assertSecured(() -> classUnderTest.uploadAdditionalContractFile(123L, null, null, null, null, null));
        assertSecured(() -> classUnderTest.uploadGeneratedGrantOfferLetterFile(123L, null, null, null, null, null));
        assertSecured(() -> classUnderTest.uploadGrantOfferLetterFile(123L, null, null, null, null, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class));
    }
}
