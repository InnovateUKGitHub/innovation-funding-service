package org.innovateuk.ifs.project.status.security;


import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.BiFunction;

import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
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
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndLead() {
        whenCompaniesHouseDetailsCompleteAndLead((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
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
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsCompleteAndLead() {
        whenCompaniesHouseDetailsCompleteAndLead((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
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
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndLead() {
        whenCompaniesHouseDetailsCompleteAndLead((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
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

    private void whenCompaniesHouseDetailsCompleteAndLead(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(setupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(setupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.ACCESSIBLE == access);

    }
}
