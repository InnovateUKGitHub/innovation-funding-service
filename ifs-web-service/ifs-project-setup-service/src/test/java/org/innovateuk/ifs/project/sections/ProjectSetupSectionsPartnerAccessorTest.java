package org.innovateuk.ifs.project.sections;

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
    private ProjectSetupProgressChecker projectSetupProgressCheckerMock;

    @InjectMocks
    private ProjectSetupSectionAccessibilityHelper accessor;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void testCheckAccessToCompaniesHouseSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessCompaniesHouseSection(organisation));

        verifyInteractions(mock -> mock.isCompaniesHouseSectionRequired(organisation));
    }

    @Test
    public void testCheckAccessToCompaniesHouseSectionButOrganisationIsNotBusiness() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);

        assertEquals(NOT_REQUIRED, accessor.canAccessCompaniesHouseSection(organisation));

        verifyInteractions(mock -> mock.isCompaniesHouseSectionRequired(organisation));
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPathForBusinessOrganisation() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionHappyPathForNonBusinessTypeOrganisation() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(mock -> mock.isCompaniesHouseSectionRequired(organisation));
    }

    @Test
    public void testCheckAccessToProjectDetailsSectionButCompaniesHouseSectionIncomplete() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessProjectDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessMonitoringOfficerSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted()
        );
    }

    @Test
    public void testCheckAccessToMonitoringOfficerSectionButProjectDetailsSectionIncomplete() {
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, accessor.canAccessMonitoringOfficerSection(organisation));
    }

    @Test
    public void testCheckAccessToBankDetailsSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);

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

        when(projectSetupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(false);

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

        when(projectSetupProgressCheckerMock.isOrganisationRequiringFunding(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessBankDetailsSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isOrganisationRequiringFunding(organisation)
        );
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionHappyPathWhenFinanceContactSubmitted() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(true);

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

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isFinanceContactSubmitted(organisation)).thenReturn(false);

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

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);

         when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(true);

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

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(mock -> mock.isLeadPartnerOrganisation(organisation));


    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPathForNonLeadPartner() {

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionHappyPathForNonLeadPartnerNonBusinessType() {

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(false);

        assertEquals(ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation)
        );
    }

    @Test
    public void testCheckAccessToOtherDocumentsSectionButNonLeadPartnerNotCompletedCompaniesHouseInformation() {

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessOtherDocumentsSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation)
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(true);

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

        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOtherDocumentsApproved(),
                mock -> mock.isGrantOfferLetterAvailable()
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionSpendProfilesNotApproved() {

        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileApproved()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved()
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionOtherDocumentsNotApproved() {

        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOtherDocumentsApproved()
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionGrantOfferNotSent() {

        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileApproved()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isGrantOfferLetterSent()).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isSpendProfileApproved(),
                mock -> mock.isOtherDocumentsApproved(),
                mock -> mock.isGrantOfferLetterAvailable(),
                mock -> mock.isGrantOfferLetterSent()
        );
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<ProjectSetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(projectSetupProgressCheckerMock)));
        verifyNoMoreInteractions(projectSetupProgressCheckerMock);
    }
}
