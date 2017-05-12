package org.innovateuk.ifs.project.sections;


import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.BiFunction;

import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Mockito.when;

public class ProjectSetupSectionAccessibilityHelperTest extends BaseUnitTest {

    @Mock
    private ProjectSetupProgressChecker projectSetupProgressCheckerMock;

    @InjectMocks
    private ProjectSetupSectionAccessibilityHelper helper;

    private OrganisationResource organisation = newOrganisationResource().build();

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndProjectDetailsComplete() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsComplete((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndNotLead((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectManagerPageWhenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndLead() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndLead((helper, organisation) -> helper.leadCanAccessProjectManagerPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsCompleteAndProjectDetailsComplete() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsComplete((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndNotLead((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectStartDatePageWhenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndLead() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndLead((helper, organisation) -> helper.leadCanAccessProjectStartDatePage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsNotComplete() {
        whenCompaniesHouseDetailsNotComplete((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndProjectDetailsComplete() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsComplete((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndNotLead() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndNotLead((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    @Test
    public void testLeadCanAccessProjectAddressPageWhenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndLead() {
        whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndLead((helper, organisation) -> helper.leadCanAccessProjectAddressPage(organisation));
    }

    private void whenCompaniesHouseDetailsNotComplete(BiFunction<ProjectSetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndProjectDetailsComplete(BiFunction<ProjectSetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndNotLead(BiFunction<ProjectSetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(false);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.NOT_ACCESSIBLE == access);

    }

    private void whenCompaniesHouseDetailsCompleteAndProjectDetailsNotCompleteAndLead(BiFunction<ProjectSetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> methodToCall) {

        when(projectSetupProgressCheckerMock.isCompaniesHouseSectionRequired(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isCompaniesHouseDetailsComplete(organisation)).thenReturn(true);
        when(projectSetupProgressCheckerMock.isProjectDetailsSubmitted()).thenReturn(false);
        when(projectSetupProgressCheckerMock.isLeadPartnerOrganisation(organisation)).thenReturn(true);

        SectionAccess access = methodToCall.apply(helper, organisation);
        Assert.assertTrue(SectionAccess.ACCESSIBLE == access);

    }
}
