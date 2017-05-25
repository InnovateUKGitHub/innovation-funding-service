package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Consumer;

import static org.innovateuk.ifs.project.sections.SectionAccess.*;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProjectSetupSectionsPartnerAccessorTest extends BaseUnitTest {

    @Mock
    private SetupProgressChecker setupProgressCheckerMock;

    @InjectMocks
    private SetupSectionAccessibilityHelper accessor;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void testCheckAccessToCompaniesHouseSectionHappyPath() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessCompaniesHouseSection(organisation));

        verifyInteractions(mock -> mock.isCompaniesHouseSectionRequired(organisation));
    }

    @Test
    public void testCheckAccessToCompaniesHouseSectionButOrganisationIsNotBusiness() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);

        assertEquals(NOT_REQUIRED, accessor.canAccessCompaniesHouseSection(organisation));

        verifyInteractions(mock -> mock.isCompaniesHouseSectionRequired(organisation));
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPathForBusinessOrganisation() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPathForNonBusinessTypeOrganisation() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(mock -> mock.isCompaniesHouseSectionRequired(organisation));
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionButCompaniesHouseSectionIncomplete() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionHappyPath() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessMonitoringOfficerSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, accessor.canAccessMonitoringOfficerSection(organisation));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionHappyPath() {

        when(setupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOrganisationRequiringFunding(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation)
        );
    }

    @Test
    public void testCheckAccessToBankDetailsSectionButFinanceContactNotYetSubmitted() {

        when(setupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isOrganisationRequiringFunding(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation)
        );
    }

    @Test
    public void testCheckAccessToBankDetailsSectionWhenNotRequired() {

        when(setupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOrganisationRequiringFunding(organisation)
        );
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionHappyPathWhenFinanceContactSubmitted() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isFinanceContactSubmitted(organisation)
        );
    }


    @Test
    public void testCheckAccessToFinanceChecksSectionButFinanceContactNotSubmitted() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isFinanceContactSubmitted(organisation)
        );
    }

    @Test
    public void testCheckAccessToSpendProfileSectionHappyPath() {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);

         when(setupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessSpendProfileSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),
                mock -> mock.isBankDetailsApproved(organisation),
                mock -> mock.isSpendProfileGenerated()
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPathForLeadPartner() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(mock -> mock.isLeadPartnerOrganisation(organisation));


    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPathForNonLeadPartner() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPathForNonLeadPartnerNonBusinessType() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation)
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionButNonLeadPartnerNotCompletedCompaniesHouseInformation() {

        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionHappyPath() {

        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOtherDocumentsApproved(),
                mock -> mock.isGrantOfferLetterAvailable(),
                mock -> mock.isGrantOfferLetterSent()
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionNotAvailable() {

        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOtherDocumentsApproved(),
                mock -> mock.isGrantOfferLetterAvailable()
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionSpendProfilesNotApproved() {

        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved()
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionOtherDocumentsNotApproved() {

        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOtherDocumentsApproved()
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionGrantOfferNotSent() {

        when(setupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOtherDocumentsApproved(),
                mock -> mock.isGrantOfferLetterAvailable(),
                mock -> mock.isGrantOfferLetterSent()
        );
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<SetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(setupProgressCheckerMock)));
        verifyNoMoreInteractions(setupProgressCheckerMock);
    }
}
