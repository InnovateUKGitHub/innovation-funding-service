package com.worth.ifs.project.sections;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Consumer;

import static com.worth.ifs.project.sections.SectionAccess.*;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProjectSetupSectionsPartnerInternalUserTest extends BaseUnitTest {

    @Mock
    private ProjectSetupProgressChecker projectSetupProgressCheckerMock;

    @InjectMocks
    private ProjectSetupSectionInternalUser internalUser;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));

        verifyInteractions(
                mock -> mock.isProjectDetailsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionButProjectDetailsSectionIncomplete() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessProjectDetailsSection(null));
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(null));

        verifyInteractions(
                mock -> mock.isProjectDetailsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessMonitoringOfficerSection(null));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionHappyPath() {
        when(projectSetupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isBankDetailsActionRequired()).thenReturn(true);
        assertEquals(ACCESSIBLE, internalUser.canAccessBankDetailsSection(null));

        verifyInteractions(
                mock -> mock.isBankDetailsApproved(),
                mock -> mock.isBankDetailsActionRequired()
        );
    }

    @Test
    public void testCheckAccessToBankDetailsSectionButProjectDetailsSectionIncomplete() {
        when(projectSetupProgressCheckerMock.isBankDetailsApproved()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isBankDetailsActionRequired()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, internalUser.canAccessBankDetailsSection(null));
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionButProjectDetailsSectionIncomplete() {
        assertEquals(ACCESSIBLE, internalUser.canAccessFinanceChecksSection(null));
    }


    @SafeVarargs
    private final void verifyInteractions(Consumer<ProjectSetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(projectSetupProgressCheckerMock)));
        verifyNoMoreInteractions(projectSetupProgressCheckerMock);
    }
}
