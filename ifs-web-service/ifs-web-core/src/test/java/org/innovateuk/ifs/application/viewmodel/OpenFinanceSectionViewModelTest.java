package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.finance.viewmodel.AcademicFinanceViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenFinanceSectionViewModelTest {

    private OpenFinanceSectionViewModel viewModel;

    private Long sectionId = 23456L;
    private SectionResource currentSection;
    private UserResource currentUser;
    private SectionAssignableViewModel sectionAssignableViewModel;
    private NavigationViewModel navigationViewModel;
    private SectionApplicationViewModel applicationViewModel;
    Set<Long> sectionsMarkedAsComplete;
    private SectionResource yourFunding = newSectionResource().withType(SectionType.FUNDING_FINANCES).build();
    private SectionResource yourOrganisation = newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build();
    private SectionResource yourProjectCosts = newSectionResource().withType(SectionType.PROJECT_COST_FINANCES).build();

    @Before
    public void setup() {
        currentSection = newSectionResource().withId(sectionId).build();
        navigationViewModel = new NavigationViewModel();
        currentUser = newUserResource().build();
        sectionsMarkedAsComplete = Sets.newSet(1L, 2L);

        viewModel = new OpenFinanceSectionViewModel(navigationViewModel, currentSection, Boolean.TRUE, sectionId, currentUser, true);

        viewModel.setTitle("Title");
        viewModel.setCurrentSection(currentSection);
        viewModel.setResponses(Collections.emptyMap());
        viewModel.setUserIsLeadApplicant(Boolean.TRUE);
        viewModel.setCompletedSections(asList(2L));
        viewModel.setSections(asMap(sectionId, currentSection));
        viewModel.setQuestionFormInputs(asMap());
        viewModel.setSubSections(asMap());
        viewModel.setSectionQuestions(asMap());
        viewModel.setSubSectionQuestionFormInputs(asMap());
        viewModel.setSectionAssignableViewModel(sectionAssignableViewModel);
        viewModel.setSectionApplicationViewModel(applicationViewModel);
        viewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
    }

    @Test
    public void testNormalGetters() {
        assertEquals("Title", viewModel.getTitle());
        assertEquals(currentSection, viewModel.getCurrentSection());
        assertEquals(Boolean.TRUE, viewModel.getHasFinanceSection());
        assertEquals(sectionId, viewModel.getFinanceSectionId());
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(asList(2L), viewModel.getCompletedSections());
        assertEquals(asMap(sectionId, currentSection), viewModel.getSections());
        assertEquals(sectionAssignableViewModel, viewModel.getAssignable());
        assertEquals(navigationViewModel, viewModel.getNavigation());
        assertEquals(currentUser, viewModel.getCurrentUser());
        assertEquals(applicationViewModel, viewModel.getApplication());
        assertEquals(true, viewModel.isSubFinanceSection());
        assertEquals(sectionsMarkedAsComplete, viewModel.getSectionsMarkedAsComplete());
    }

    @Test
    public void testGetCurrentQuestionFormInputs() {
        assertEquals(null, viewModel.getCurrentQuestionFormInputs());
    }

    @Test
    public void testGetIsYourFinancesAndIsNotCompleted() {
        currentSection.setName("Your finances");
        viewModel.setCurrentSection(currentSection);
        viewModel.setCompletedSections(asList());

        assertEquals(Boolean.TRUE, viewModel.getIsYourFinancesAndIsNotCompleted());
    }

    @Test
    public void testGetIsYourFinances() {
        currentSection.setName("NOT Your finances");
        viewModel.setCurrentSection(currentSection);

        assertEquals(Boolean.FALSE, viewModel.getIsYourFinances());
    }

    @Test
    public void getIsFinanceOverview() {
        currentSection.setName("Finances overview");
        viewModel.setCurrentSection(currentSection);

        assertEquals(Boolean.TRUE, viewModel.getIsFinanceOverview());
    }

    @Test
    public void testGetIsSection() {
        assertEquals(Boolean.TRUE, viewModel.getIsSection());
    }

    @Test
    public void testShowSectionAsNotRequired() {
        viewModel.setNotRequestingFunding(true);

        assertFalse(viewModel.showSectionAsNotRequired(yourProjectCosts));
        assertTrue(viewModel.showSectionAsNotRequired(yourOrganisation));
        assertTrue(viewModel.showSectionAsNotRequired(yourFunding));

        viewModel.setNotRequestingFunding(false);

        assertFalse(viewModel.showSectionAsNotRequired(yourProjectCosts));
        assertFalse(viewModel.showSectionAsNotRequired(yourOrganisation));
        assertFalse(viewModel.showSectionAsNotRequired(yourFunding));
    }

    @Test
    public void testShowSectionAsLockedFunding() {
        viewModel.setFundingSectionLocked(true);
        viewModel.setNotRequestingFunding(true);

        assertFalse(viewModel.showSectionAsLockedFunding(yourProjectCosts));
        assertFalse(viewModel.showSectionAsLockedFunding(yourOrganisation));
        assertFalse(viewModel.showSectionAsLockedFunding(yourFunding));

        viewModel.setNotRequestingFunding(false);

        assertFalse(viewModel.showSectionAsLockedFunding(yourProjectCosts));
        assertFalse(viewModel.showSectionAsLockedFunding(yourOrganisation));
        assertTrue(viewModel.showSectionAsLockedFunding(yourFunding));

        viewModel.setFundingSectionLocked(false);

        assertFalse(viewModel.showSectionAsLockedFunding(yourProjectCosts));
        assertFalse(viewModel.showSectionAsLockedFunding(yourOrganisation));
        assertFalse(viewModel.showSectionAsLockedFunding(yourFunding));

    }

    @Test
    public void testShowSectionAsLink() {
        viewModel.setFundingSectionLocked(true);
        viewModel.setNotRequestingFunding(true);

        assertTrue(viewModel.showSectionAsLink(yourProjectCosts));
        assertTrue(viewModel.showSectionAsLink(yourOrganisation));
        assertTrue(viewModel.showSectionAsLink(yourFunding));

        viewModel.setNotRequestingFunding(false);

        assertTrue(viewModel.showSectionAsLink(yourProjectCosts));
        assertTrue(viewModel.showSectionAsLink(yourOrganisation));
        assertFalse(viewModel.showSectionAsLink(yourFunding));

        viewModel.setFundingSectionLocked(false);

        assertTrue(viewModel.showSectionAsLink(yourProjectCosts));
        assertTrue(viewModel.showSectionAsLink(yourOrganisation));
        assertTrue(viewModel.showSectionAsLink(yourFunding));

    }

    @Test
    public void testShowSectionStatus() {
        viewModel.setFundingSectionLocked(true);
        viewModel.setNotRequestingFunding(true);

        assertTrue(viewModel.showSectionStatus(yourProjectCosts));
        assertTrue(viewModel.showSectionStatus(yourOrganisation));
        assertTrue(viewModel.showSectionStatus(yourFunding));

        viewModel.setNotRequestingFunding(false);

        assertTrue(viewModel.showSectionStatus(yourProjectCosts));
        assertTrue(viewModel.showSectionStatus(yourOrganisation));
        assertFalse(viewModel.showSectionStatus(yourFunding));

        viewModel.setFundingSectionLocked(false);

        assertTrue(viewModel.showSectionStatus(yourProjectCosts));
        assertTrue(viewModel.showSectionStatus(yourOrganisation));
        assertTrue(viewModel.showSectionStatus(yourFunding));

    }

    @Test
    public void testGetOrganisationSizeAlert() {
        assertEquals(Boolean.FALSE, viewModel.getOrganisationSizeAlert());

        Long fundingSectionId = 2738L;
        List<SectionResource> fundingSections = newSectionResource().withId(fundingSectionId).withType(SectionType.FUNDING_FINANCES).build(1);
        viewModel.setFundingSection(fundingSections.stream().findFirst().orElse(null));

        assertEquals(Boolean.FALSE, viewModel.getOrganisationSizeAlert());

        sectionsMarkedAsComplete.add(fundingSectionId);

        assertEquals(Boolean.TRUE, viewModel.getOrganisationSizeAlert());
    }

    @Test
    public void testOrganisationFinancesIsNotDisplayedWhenFinanceModelIsAcademic() {
        SectionResource section = newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build();
        viewModel.setFinanceViewModel(new AcademicFinanceViewModel());
        assertEquals(Boolean.FALSE, viewModel.isSectionDisplayed(section));
    }

    @Test
    public void testOrganisationFinancesIsDisplayedWhenFinanceModelIsNotAcademic() {
        SectionResource section = newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build();
        viewModel.setFinanceViewModel(new FinanceViewModel());
        assertEquals(Boolean.TRUE, viewModel.isSectionDisplayed(section));
    }
}
