package org.innovateuk.ifs.project.status.security;


import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.BiFunction;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SetupSectionAccessibilityHelperTest extends BaseUnitTest {

    @Mock
    private SetupProgressChecker setupProgressChecker;

    @InjectMocks
    private SetupSectionAccessibilityHelper helper;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void leadCanAccessProjectManagerPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete(SetupSectionAccessibilityHelper::leadCanAccessProjectManagerPage);
    }

    @Test
    public void leadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndNotLead(SetupSectionAccessibilityHelper::leadCanAccessProjectManagerPage);
    }

    @Test
    public void leadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated(SetupSectionAccessibilityHelper::leadCanAccessProjectManagerPage);
    }

    @Test
    public void leadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated(SetupSectionAccessibilityHelper::leadCanAccessProjectManagerPage);
    }

    @Test
    public void leadCanAccessProjectAddressPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete(SetupSectionAccessibilityHelper::leadCanAccessProjectAddressPage);
    }

    @Test
    public void leadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndNotLead(SetupSectionAccessibilityHelper::leadCanAccessProjectAddressPage);
    }

    @Test
    public void leadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated(SetupSectionAccessibilityHelper::leadCanAccessProjectAddressPage);
    }

    @Test
    public void leadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated(SetupSectionAccessibilityHelper::leadCanAccessProjectAddressPage);
    }

    @Test
    public void canAccessFinanceContactPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete(SetupSectionAccessibilityHelper::canAccessFinanceContactPage);
    }

    @Test
    public void canAccessFinanceContactPageWhenCompaniesHouseDetailsCompleteAndGOLGenerated() {
        whenCompaniesHouseDetailsCompleteAndGolGenerated(SetupSectionAccessibilityHelper::canAccessFinanceContactPage);
    }

    @Test
    public void canAccessFinanceContactPageWhenCompaniesHouseDetailsCompleteAndGOLNotGenerated() {
        whenCompaniesHouseDetailsCompleteAndGolNotGenerated(SetupSectionAccessibilityHelper::canAccessFinanceContactPage);
    }

    @Test
    public void canAccessSpendProfileSectionWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete(SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @Test
    public void canAccessSpendProfileSectionWhenProjectDetailsNotSubmitted() {
        whenProjectDetailsNotSubmitted(SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @Test
    public void canAccessSpendProfileSectionWhenBankDetailsNotApproved() {
        whenBankDetailsNotApproved(SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @Test
    public void canAccessSpendProfileSectionWhenSpendProfileNotGenerated() {
        whenSpendProfileNotGenerated(SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @Test
    public void canAccessSpendProfileSectionWhenSpendProfileGenerated() {
        whenSpendProfileGeneratedAndAccessible(SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @Test
    public void canEditSpendProfileSectionWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void canEditSpendProfileSectionWhenProjectDetailsNotSubmitted() {
        whenProjectDetailsNotSubmitted((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void canEditSpendProfileSectionWhenBankDetailsNotApproved() {
        whenBankDetailsNotApproved((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void canEditSpendProfileSectionWhenSpendProfileNotGenerated() {
        whenSpendProfileNotGenerated((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void canEditSpendProfileSectionWhenUserNotFromCurrentOrganisation() {
        whenSpendProfileGeneratedAndNotAccessible((helper, organisation) -> helper.canEditSpendProfileSection(organisation, 22L));
    }

    @Test
    public void canEditSpendProfileSectionWhenUserFromCurrentOrganisation() {
        whenSpendProfileGeneratedAndAccessible((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void canAccessFinanceChecksSectionWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete(SetupSectionAccessibilityHelper::canAccessFinanceChecksSection);
    }

    @Test
    public void canAccessFinanceChecksSectionWhenFinanceContactNotYetSubmitted() {
        whenFinanceContactNotSubmitted(SetupSectionAccessibilityHelper::canAccessFinanceChecksSection);
    }

    @Test
    public void canAccessFinanceChecksSectionWhenFinanceContactSubmitted() {
        whenFinanceContactSubmitted(SetupSectionAccessibilityHelper::canAccessFinanceChecksSection);
    }

    @Test
    public void canAccessDocumentsSectionWhenLead() {
        doTest(SetupSectionAccessibilityHelper::canAccessDocumentsSection,
                false, false, true, ACCESSIBLE);
    }

    @Test
    public void canAccessDocumentsSectionWhenNotLeadAndCompaniesHouseSectionRequiredButNotComplete() {
        doTest(SetupSectionAccessibilityHelper::canAccessDocumentsSection,
                true, false, false, NOT_ACCESSIBLE);
    }

    @Test
    public void canAccessDocumentsSectionWhenNotLeadAndCompaniesHouseSectionNotRequired() {
        doTest(SetupSectionAccessibilityHelper::canAccessDocumentsSection,
                false, false, false, ACCESSIBLE);
    }

    @Test
    public void canAccessDocumentsSectionWhenNotLeadAndCompaniesHouseSectionRequiredAndComplete() {
        doTest(SetupSectionAccessibilityHelper::canAccessDocumentsSection,
                true, true, false, ACCESSIBLE);
    }

    private void doTest(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall,
                        boolean companiesHouseSectionRequired, boolean companiesHouseDetailsComplete, boolean lead,
                        SectionAccess expectedAccess) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(companiesHouseSectionRequired);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(companiesHouseDetailsComplete);
        when(setupProgressChecker.isLeadPartnerOrganisation(organisation)).thenReturn(lead);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(expectedAccess, access);

    }

    @Test
    public void canAccessGrantOfferLetterSectionWhenDocsApproved() {
        doTest(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                true, true, true, true,true, ACCESSIBLE);
    }

    @Test
    public void canNotAccessGrantOfferLetterSectionWhenDocsNotApproved() {
        doTest(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                true, false, true, true, true, NOT_ACCESSIBLE);
    }

    @Test
    public void canAccessGrantOfferLetterSectionWhenSpendProfileNotApproved() {
        doTest(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                false, true, true, true, true, NOT_ACCESSIBLE);
    }

    @Test
    public void canAccessGrantOfferLetterSectionWhenBankDetailsNotApproved() {
        doTest(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
               true, true, false, true, true, NOT_ACCESSIBLE);
    }

    @Test
    public void canAccessGrantOfferLetterSectionWhenGOLNotAvailable() {
        doTest(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                true, true, true,false, true, NOT_ACCESSIBLE);
    }

    @Test
    public void canAccessGrantOfferLetterSectionWhenGOLNotSent() {
        doTest(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                true, true, true, true, false, NOT_ACCESSIBLE);
    }

    @Test
    public void canAccessBankDetailsSection() {
        when(setupProgressChecker.isOfflineOrWithdrawn()).thenReturn(false);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(setupProgressChecker.isFinanceContactSubmitted(organisation)).thenReturn(true);

        SectionAccess access = helper.canAccessBankDetailsSection(organisation);

        assertEquals(SectionAccess.ACCESSIBLE, access);
    }

    @Test
    public void internationalCannotAccessBankDetailsSection() {
        organisation.setInternational(true);
        when(setupProgressChecker.isOfflineOrWithdrawn()).thenReturn(false);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isOrganisationRequiringFunding(organisation)).thenReturn(true);
        when(setupProgressChecker.isFinanceContactSubmitted(organisation)).thenReturn(true);

        SectionAccess access = helper.canAccessBankDetailsSection(organisation);

        assertEquals(NOT_ACCESSIBLE, access);
    }

    private void doTest(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall,
                        boolean spendProfileApproved, boolean docsApproved, boolean bankDetailsApproved, boolean golAvailable, boolean golSent,
                        SectionAccess expectedAccess) {

        when(setupProgressChecker.isSpendProfileApproved()).thenReturn(spendProfileApproved);
        when(setupProgressChecker.isDocumentsApproved()).thenReturn(docsApproved);
        when(setupProgressChecker.isBankDetailsApproved(organisation)).thenReturn(bankDetailsApproved);
        when(setupProgressChecker.isGrantOfferLetterAvailable()).thenReturn(golAvailable);
        when(setupProgressChecker.isGrantOfferLetterSent()).thenReturn(golSent);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(expectedAccess, access);

    }

    private void whenCompaniesHouseDetailsNotComplete(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenCompaniesHouseDetailsCompleteAndNotLead(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isLeadPartnerOrganisation(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenCompaniesHouseDetailsCompleteAndGolGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        when(setupProgressChecker.isGrantOfferLetterAvailable()).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenCompaniesHouseDetailsCompleteAndGolNotGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        when(setupProgressChecker.isGrantOfferLetterAvailable()).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(ACCESSIBLE, access);

    }

    private void whenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterAvailable()).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(setupProgressChecker.isGrantOfferLetterAvailable()).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(ACCESSIBLE, access);

    }

    private void whenProjectDetailsNotSubmitted(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isProjectDetailsSubmitted()).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenFinanceContactNotSubmitted(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isFinanceContactSubmitted(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenFinanceContactSubmitted(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isFinanceContactSubmitted(organisation)).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(ACCESSIBLE, access);

    }

    private void whenBankDetailsNotApproved(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressChecker.isProjectDetailsSubmitted()).thenReturn(true);
        when(setupProgressChecker.isBankDetailsApproved(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenSpendProfileNotGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        setUpMocking(true, true, true, true, true, false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenSpendProfileGeneratedAndNotAccessible(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        setUpMocking(true, true, true, true,true, true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(SectionAccess.NOT_ACCESSIBLE, access);

    }

    private void whenSpendProfileGeneratedAndAccessible(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        setUpMocking(true, true, true, true,true, true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        assertEquals(ACCESSIBLE, access);

    }

    private void setUpMocking(boolean companiesHouseSectionRequired, boolean companiesHouseDetailsComplete,
                              boolean projectDetailsSubmitted, boolean projectTeamSectionComplete,
                              boolean bankDetailsApproved, boolean spendProfileGenerated) {

        when(setupProgressChecker.isCompaniesHouseSectionRequired(organisation)).thenReturn(companiesHouseSectionRequired);
        when(setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)).thenReturn(companiesHouseDetailsComplete);
        when(setupProgressChecker.isProjectDetailsSubmitted()).thenReturn(projectDetailsSubmitted);
        when(setupProgressChecker.isProjectTeamCompleted()).thenReturn(projectTeamSectionComplete);
        when(setupProgressChecker.isBankDetailsApproved(organisation)).thenReturn(bankDetailsApproved);
        when(setupProgressChecker.isSpendProfileGenerated()).thenReturn(spendProfileGenerated);
    }
}
