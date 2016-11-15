package com.worth.ifs.project.sections;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Consumer;

import static com.worth.ifs.project.sections.SectionAccess.*;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProjectSetupSectionsPartnerAccessorTest extends BaseUnitTest {

    @Mock
    private ProjectSetupProgressChecker projectSetupProgressCheckerMock;

    @InjectMocks
    private ProjectSetupSectionPartnerAccessor accessor;

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
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isFinanceContactSubmitted(organisation)
        );
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionHappyPathWhenBankDetailsApproved() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);

        // TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable
        // when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted()

                // TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable
                // mock -> mock.isBankDetailsApproved(organisation)
        );
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionHappyPathWhenBankDetailsQueried() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);


        // TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable
//        when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(false);
//        when(projectSetupProgressCheckerMock.isBankDetailsQueried(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted()


                // TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable
//                mock -> mock.isBankDetailsApproved(organisation),
//                mock -> mock.isBankDetailsQueried(organisation)
        );
    }

    @Test
    @Ignore("TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable")
    public void testCheckAccessToFinanceChecksSectionButBankDetailsNotApprovedOrQueried() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isBankDetailsQueried(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));
    }

    @Test
    public void testCheckAccessToFinanceChecksSectionButSpendProfileNotYetGenerated() {

        when(projectSetupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(false);
        assertEquals(NOT_ACCESSIBLE, accessor.canAccessFinanceChecksSection(organisation));
    }

    @Test
    public void testCheckAccessToSpendProfileSectionHappyPath() {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);


        // TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable
        // when(projectSetupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessSpendProfileSection(organisation));

        verifyInteractions(
                mock -> mock.isCompaniesHouseSectionRequired(organisation),
                mock -> mock.isCompaniesHouseDetailsComplete(organisation),
                mock -> mock.isProjectDetailsSubmitted(),

                // TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable
                // mock -> mock.isBankDetailsApproved(organisation),
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
    public void testCheckAccessToGrantOfferLetterSectionLeadPartnerHappyPath() {

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfilesApproved(organisation)).thenReturn(true);

        assertEquals(ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isSpendProfilesApproved(organisation),
                mock -> mock.isOtherDocumentsApproved(organisation)
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionNonLeadPartner() {

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfilesApproved(organisation)).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation)
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionLeadPartnerSpendProfilesNotApproved() {

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isSpendProfilesApproved(organisation)).thenReturn(false);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isSpendProfilesApproved(organisation)
        );
    }

    @Test
    public void testCheckAccessToGrantOfferLetterSectionLeadPartnerOtherDocumentsNotApproved() {

        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isOtherDocumentsApproved(organisation)).thenReturn(false);
        when(projectSetupProgressCheckerMock.isSpendProfilesApproved(organisation)).thenReturn(true);

        assertEquals(NOT_ACCESSIBLE, accessor.canAccessGrantOfferLetterSection(organisation));

        verifyInteractions(
                mock -> mock.isLeadPartnerOrganisation(organisation),
                mock -> mock.isSpendProfilesApproved(organisation),
                mock -> mock.isOtherDocumentsApproved(organisation)
        );
    }

    @SafeVarargs
    private final void verifyInteractions(Consumer<ProjectSetupProgressChecker>... verifiers) {
        asList(verifiers).forEach(verifier -> verifier.accept(verify(projectSetupProgressCheckerMock)));
        verifyNoMoreInteractions(projectSetupProgressCheckerMock);
    }
}
