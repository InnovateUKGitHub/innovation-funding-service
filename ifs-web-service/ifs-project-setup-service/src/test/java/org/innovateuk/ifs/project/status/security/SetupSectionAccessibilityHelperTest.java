package org.innovateuk.ifs.project.status.security;


import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.BiFunction;

import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Mockito.when;

public class SetupSectionAccessibilityHelperTest extends BaseUnitTest {

    @Mock
    private SetupProgressChecker setupProgressCheckerMock;

    @InjectMocks
    private SetupSectionAccessibilityHelper helper;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndNotLead((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndNotLead((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsCompleteAndLeadAndSpendProfileGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndSpendProfileGenerated((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsCompleteAndLeadAndSpendProfileNotGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndSpendProfileNotGenerated((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndNotLead((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated() {
        whenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    @Test
    public void testCanAccessFinanceContactPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.canAccessFinanceContactPage(organisation));
    }

    @Test
    public void testCanAccessFinanceContactPageWhenCompaniesHouseDetailsCompleteAndGOLGenerated() {
        whenCompaniesHouseDetailsCompleteAndGolGenerated((helper, organisation) -> helper.canAccessFinanceContactPage(organisation));
    }

    @Test
    public void testCanAccessFinanceContactPageWhenCompaniesHouseDetailsCompleteAndGOLNotGenerated() {
        whenCompaniesHouseDetailsCompleteAndGolNotGenerated((helper, organisation) -> helper.canAccessFinanceContactPage(organisation));
    }

    @Test
    public void testCanAccessSpendProfileSectionWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.canAccessSpendProfileSection(organisation));
    }

    @Test
    public void testCanAccessSpendProfileSectionWhenProjectDetailsNotSubmitted() {
        whenProjectDetailsNotSubmitted((helper, organisation) -> helper.canAccessSpendProfileSection(organisation));
    }

    @Test
    public void testCanAccessSpendProfileSectionWhenBankDetailsNotApproved() {
        whenBankDetailsNotApproved((helper, organisation) -> helper.canAccessSpendProfileSection(organisation));
    }

    @Test
    public void testCanAccessSpendProfileSectionWhenSpendProfileNotGenerated() {
        whenSpendProfileNotGenerated((helper, organisation) -> helper.canAccessSpendProfileSection(organisation));
    }

    @Test
    public void testCanAccessSpendProfileSectionWhenSpendProfileGenerated() {
        whenSpendProfileGeneratedAndAccessible((helper, organisation) -> helper.canAccessSpendProfileSection(organisation));
    }

    @Test
    public void testCanEditSpendProfileSectionWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void testCanEditSpendProfileSectionWhenProjectDetailsNotSubmitted() {
        whenProjectDetailsNotSubmitted((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void testCanEditSpendProfileSectionWhenBankDetailsNotApproved() {
        whenBankDetailsNotApproved((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void testCanEditSpendProfileSectionWhenSpendProfileNotGenerated() {
        whenSpendProfileNotGenerated((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    @Test
    public void testCanEditSpendProfileSectionWhenUserNotFromCurrentOrganisation() {
        whenSpendProfileGeneratedAndNotAccessible((helper, organisation) -> helper.canEditSpendProfileSection(organisation, 22L));
    }

    @Test
    public void testCanEditSpendProfileSectionWhenUserFromCurrentOrganisation() {
        whenSpendProfileGeneratedAndAccessible((helper, organisation) -> helper.canEditSpendProfileSection(organisation, organisation.getId()));
    }

    private void whenCompaniesHouseDetailsNotComplete(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndNotLead(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndLeadAndSpendProfileGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndLeadAndSpendProfileNotGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndGolGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndGolNotGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);

        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndLeadAndGolGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndLeadAndGolNotGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isGrantOfferLetterAvailable()).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.ACCESSIBLE == access);

    }

    private void whenProjectDetailsNotSubmitted(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenBankDetailsNotApproved(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);
        when(setupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenSpendProfileNotGenerated(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        setUpMocking(true, true, true, true, false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenSpendProfileGeneratedAndNotAccessible(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        setUpMocking(true, true, true, true, true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenSpendProfileGeneratedAndAccessible(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        setUpMocking(true, true, true, true, true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.ACCESSIBLE == access);

    }

    private void setUpMocking(boolean companiesHouseSectionRequired, boolean companiesHouseDetailsComplete,
                              boolean projectDetailsSubmitted, boolean bankDetailsApproved, boolean spendProfileGenerated) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(companiesHouseSectionRequired);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(companiesHouseDetailsComplete);
        when(setupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(projectDetailsSubmitted);
        when(setupProgressCheckerMock.isBankDetailsApproved(organisation)).thenReturn(bankDetailsApproved);
        when(setupProgressCheckerMock.isSpendProfileGenerated()).thenReturn(spendProfileGenerated);
    }
}
