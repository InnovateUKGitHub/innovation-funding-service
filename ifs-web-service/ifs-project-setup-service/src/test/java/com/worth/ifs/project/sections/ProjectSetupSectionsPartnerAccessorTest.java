package com.worth.ifs.project.sections;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Consumer;

import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectSetupSectionsPartnerAccessorTest extends BaseUnitTest {

    @Mock
    private ProjectSetupProgressChecker projectSetupProgressCheckerMock;

    @InjectMocks
    private ProjectSetupSectionPartnerAccessor accessor;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void testCheckAccessToCompaniesHouseSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);

        assertTrue(accessor.checkAccessToCompaniesHouseSection(organisation));

        verifyInteractions(mock -> mock.isBusinessOrganisationType(organisation));
    }

    public void testCheckAccessToCompaniesHouseSectionButOrganisationIsNotBusiness() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(false);

        assertFalse(accessor.checkAccessToCompaniesHouseSection(organisation));

        verifyInteractions(mock -> mock.isBusinessOrganisationType(organisation));
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPathForBusinessOrganisation() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        assertTrue(accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isBusinessOrganisationType(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPathForNonBusinessTypeOrganisation() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(false);

        assertTrue(accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(mock -> mock.isBusinessOrganisationType(organisation));
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionButCompaniesHouseSectionIncomplete() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        assertFalse(accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isBusinessOrganisationType(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);

        assertTrue(accessor.checkAccessToMonitoringOfficerSection(organisation));

        verifyInteractions(
                mock -> mock.isBusinessOrganisationType(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertFalse(accessor.checkAccessToMonitoringOfficerSection(organisation));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);

        assertTrue(accessor.checkAccessToBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isBusinessOrganisationType(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation)
        );
    }

    public void testCheckAccessToBankDetailsSectionButFinanceContactNotYetSubmitted() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(false);

        assertFalse(accessor.checkAccessToBankDetailsSection(organisation));
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionHappyPathWhenBankDetailsApproved() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);

        assertTrue(accessor.checkAccessToFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isBusinessOrganisationType(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isBankDetailsApproved(organisation)
        );
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionHappyPathWhenBankDetailsQueried() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isBankDetailsQueried(organisation)).thenReturn(true);

        assertTrue(accessor.checkAccessToFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isBusinessOrganisationType(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isBankDetailsApproved(organisation),
                mock -> mock.isBankDetailsQueried(organisation)
        );
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionButBankDetailsNotApprovedOrQueried() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isBankDetailsQueried(organisation)).thenReturn(false);

        assertFalse(accessor.checkAccessToFinanceChecksSection(organisation));
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionButSpendProfileNotYetGenerated() {

        when(projectSetupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(false);
        assertFalse(accessor.checkAccessToFinanceChecksSection(organisation));
    }

    @Test
    public void testCheckAccessToSpendProfileSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isBusinessOrganisationType(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(true);

        assertTrue(accessor.checkAccessToSpendProfileSection(organisation));

        verifyInteractions(
                mock -> mock.isBusinessOrganisationType(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isBankDetailsApproved(organisation),
                mock -> mock.isSpendProfileGenerated()
        );
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<ProjectSetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(projectSetupProgressCheckerMock)));
        verifyNoMoreInteractions(projectSetupProgressCheckerMock);
    }
}
