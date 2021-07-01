package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.sections.SectionAccess.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SetupSectionsPartnerAccessorTest extends BaseUnitTest {

    @Mock
    private SetupProgressChecker setupProgressCheckerMock;

    @InjectMocks
    private SetupSectionAccessibilityHelper accessor;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void checkAccessToCompaniesHouseSectionHappyPath() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessCompaniesHouseSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToCompaniesHouseSectionButOrganisationIsNotBusiness() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_REQUIRED, accessor.canAccessCompaniesHouseSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToProjectDetailsSectionHappyPathForBusinessOrganisation() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToProjectDetailsSectionHappyPathForNonBusinessTypeOrganisation() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOfflineOrWithdrawn()
                );
    }

    @Test
    public void checkAccessToProjectDetailsSectionButCompaniesHouseSectionIncomplete() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void canAccessPartnerProjectLocationPageWhenCompaniesHouseIsIncomplete() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessPartnerProjectLocationPage(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void isPartnerProjectLocationSubmittedFailure() {

        when(setupProgressCheckerMock.isPartnerProjectLocationSubmitted(organisation)).thenReturn(false);

        assertFalse(accessor.isPartnerProjectLocationSubmitted(organisation));

        verifyInteractions(mock -> mock.isPartnerProjectLocationSubmitted(organisation));
    }

    @Test
    public void isPartnerProjectLocationSubmittedSuccess() {

        when(setupProgressCheckerMock.isPartnerProjectLocationSubmitted(organisation)).thenReturn(true);

        assertTrue(accessor.isPartnerProjectLocationSubmitted(organisation));

        verifyInteractions(mock -> mock.isPartnerProjectLocationSubmitted(organisation));
    }

    @Test
    public void isMonitoringOfficerAssignedFailure() {

        when(setupProgressCheckerMock.isMonitoringOfficerAssigned()).thenReturn(false);

        assertFalse(accessor.isMonitoringOfficerAssigned());

        verifyInteractions(mock -> mock.isMonitoringOfficerAssigned());
    }

    @Test
    public void isMonitoringOfficerAssignedSuccess() {

        when(setupProgressCheckerMock.isMonitoringOfficerAssigned()).thenReturn(true);

        assertTrue(accessor.isMonitoringOfficerAssigned());

        verifyInteractions(mock -> mock.isMonitoringOfficerAssigned());
    }

    @Test
    public void checkAccessToBankDetailsSectionHappyPath() {

        when(setupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOrganisationRequiringFunding(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToBankDetailsSectionButFinanceContactNotYetSubmitted() {

        when(setupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOrganisationRequiringFunding(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToBankDetailsSectionWhenNotRequired() {

        when(setupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOrganisationRequiringFunding(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToFinanceChecksSectionHappyPathWhenFinanceContactSubmitted() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }


    @Test
    public void checkAccessToFinanceChecksSectionButFinanceContactNotSubmitted() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToSpendProfileSectionHappyPath() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isProjectTeamCompleted()).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);
        when(setupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessSpendProfileSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isProjectTeamCompleted(),
                mock -> mock.isSpendProfileGenerated(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToSpendProfileSectionProjectTeamIncomplete() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isProjectTeamCompleted()).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);
        when(setupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessSpendProfileSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isProjectTeamCompleted(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }


    @Test
    public void checkAccessToDocumentsSectionHappyPathForLeadPartner() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );


    }

    @Test
    public void checkAccessToDocumentsSectionHappyPathForNonLeadPartner() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToDocumentsSectionHappyPathForNonLeadPartnerNonBusinessType() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToDocumentsSectionButNonLeadPartnerNotCompletedCompaniesHouseInformation() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionHappyPath() {

        when(setupProgressCheckerMock.isDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isDocumentsApproved(),
                mock -> mock.isBankDetailsApproved(organisation),
                mock -> mock.isGrantOfferLetterAvailable(),
                mock -> mock.isGrantOfferLetterSent(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionNotAvailable() {

        when(setupProgressCheckerMock.isDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isDocumentsApproved(),
                mock -> mock.isBankDetailsApproved(organisation),
                mock -> mock.isGrantOfferLetterAvailable(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionSpendProfilesNotApproved() {

        when(setupProgressCheckerMock.isDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionOtherDocumentsNotApproved() {

        when(setupProgressCheckerMock.isDocumentsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isDocumentsApproved(),
                mock -> mock.isDocumentsApproved(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionBankDetailsNotApproved() {

        when(setupProgressCheckerMock.isDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isDocumentsApproved(),
                mock -> mock.isBankDetailsApproved(organisation),
                mock -> mock.isDocumentsApproved(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @Test
    public void checkAccessToGrantOfferLetterSectionGrantOfferNotSent() {

        when(setupProgressCheckerMock.isDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(false);
        when(setupProgressCheckerMock.isOfflineOrWithdrawn()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isDocumentsApproved(),
                mock -> mock.isBankDetailsApproved(organisation),
                mock -> mock.isGrantOfferLetterAvailable(),
                mock -> mock.isGrantOfferLetterSent(),
                mock -> mock.isOfflineOrWithdrawn()
        );
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<SetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(setupProgressCheckerMock)));
        verifyNoMoreInteractions(setupProgressCheckerMock);
    }
}
