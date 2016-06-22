package com.worth.ifs.service;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.controller.form.competitionsetup.AdditionalInfoForm;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.service.competitionsetup.formpopulator.CompetitionSetupFormPopulator;
import com.worth.ifs.service.competitionsetup.sectionupdaters.CompetitionSetupSectionSaver;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;
import com.worth.ifs.util.CollectionFunctions;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupServiceImplTest {

	@InjectMocks
	private CompetitionSetupServiceImpl service;
	
	@Mock
	private CompetitionService competitionService;

	@Mock
	private UserService userService;

	@Mock
	private CategoryService categoryService;
	
	@Mock
	private CategoryFormatter categoryFormatter;
	
	private List<CompetitionSetupSection> completedSections;
	
	@Test
	public void testPopulateCompetitionSectionModelAttributesInitial() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.build();
		CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
		
		setUpCommonModelItems();
		List<UserResource> compExecs = new ArrayList<>();
		when(userService.findUserByType(UserRoleType.COMP_EXEC)).thenReturn(compExecs);
		List<CategoryResource> innovationSectors = new ArrayList<>();
		when(categoryService.getCategoryByType(CategoryType.INNOVATION_SECTOR)).thenReturn(innovationSectors);
		List<CategoryResource> innovationAreas = new ArrayList<>();
		when(categoryService.getCategoryByType(CategoryType.INNOVATION_AREA)).thenReturn(innovationAreas);
		List<CompetitionTypeResource> competitionTypes = new ArrayList<>();
		when(competitionService.getAllCompetitionTypes()).thenReturn(competitionTypes);
		List<UserResource> leadTechs = new ArrayList<>();
		when(userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST)).thenReturn(leadTechs);
		
		service.populateCompetitionSectionModelAttributes(model, competition, section);
		
		assertEquals(12, model.asMap().size());
		verifyCommonModelItems(model, competition, section);
		assertEquals("section-initial", model.asMap().get("currentSectionFragment"));
		assertEquals(compExecs, model.asMap().get("competitionExecutiveUsers"));
		assertEquals(innovationSectors, model.asMap().get("innovationSectors"));
		assertEquals(innovationAreas, model.asMap().get("innovationAreas"));
		assertEquals(competitionTypes, model.asMap().get("competitionTypes"));
		assertEquals(leadTechs, model.asMap().get("competitionLeadTechUsers"));
	}
	
	@Test
	public void testPopulateCompetitionSectionModelAttributesEligibility() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();
		CompetitionSetupSection section = CompetitionSetupSection.ELIGIBILITY;
		
		setUpCommonModelItems();
		
		List<CategoryResource> researchCategories = new ArrayList<>();
		when(categoryService.getCategoryByType(CategoryType.RESEARCH_CATEGORY)).thenReturn(researchCategories);
		when(categoryFormatter.format(CollectionFunctions.asLinkedSet(2L, 3L), researchCategories)).thenReturn("formattedcategories");

		service.populateCompetitionSectionModelAttributes(model, competition, section);
		
		assertEquals(12, model.asMap().size());
		verifyCommonModelItems(model, competition, section);
		assertEquals("section-eligibility", model.asMap().get("currentSectionFragment"));
		assertArrayEquals(ResearchParticipationAmount.values(), (Object[])model.asMap().get("researchParticipationAmounts"));
		assertArrayEquals(CollaborationLevel.values(), (Object[])model.asMap().get("collaborationLevels"));
		assertArrayEquals(LeadApplicantType.values(), (Object[])model.asMap().get("leadApplicantTypes"));
		assertEquals(researchCategories, model.asMap().get("researchCategories"));
		assertEquals("formattedcategories", model.asMap().get("researchCategoriesFormatted"));
	}
	
	private void setUpCommonModelItems() {
		completedSections = new ArrayList<>();
		when(competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(8L)).thenReturn(completedSections);
	}
	
	private void verifyCommonModelItems(Model model, CompetitionResource competition, CompetitionSetupSection section) {
		assertEquals(Boolean.TRUE, model.asMap().get("editable"));
		assertEquals(competition, model.asMap().get("competition"));
		assertEquals(section, model.asMap().get("currentSection"));
		assertArrayEquals(CompetitionSetupSection.values(), (Object[])model.asMap().get("allSections"));
		assertEquals(completedSections, model.asMap().get("allCompletedSections"));
		assertEquals("code: name", model.asMap().get("subTitle"));
	}
	
	
	
	@Test
	public void testGetSectionFormData() {
		CompetitionResource competitionResource = newCompetitionResource().build();
		
		CompetitionSetupFormPopulator matchingPopulator = mock(CompetitionSetupFormPopulator.class);
		when(matchingPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
		CompetitionSetupForm matchingForm = mock(CompetitionSetupForm.class);
		when(matchingPopulator.populateForm(competitionResource)).thenReturn(matchingForm);
		
		CompetitionSetupFormPopulator otherPopulator = mock(CompetitionSetupFormPopulator.class);
		when(otherPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
		CompetitionSetupForm otherForm = mock(CompetitionSetupForm.class);
		when(otherPopulator.populateForm(competitionResource)).thenReturn(otherForm);

		service.setCompetitionSetupFormPopulators(asList(matchingPopulator, otherPopulator));
		
		CompetitionSetupForm result = service.getSectionFormData(competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);
		
		assertEquals(matchingForm, result);
		verify(matchingPopulator).populateForm(competitionResource);
		verify(otherPopulator, never()).populateForm(competitionResource);
	}
	
	@Test
	public void testSaveSection() {
		CompetitionSetupForm competitionSetupForm = new AdditionalInfoForm();
		CompetitionResource competitionResource = newCompetitionResource().build();
		
		CompetitionSetupSectionSaver matchingSaver = mock(CompetitionSetupSectionSaver.class);
		when(matchingSaver.sectionToSave()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
		when(matchingSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(true);
		
		CompetitionSetupSectionSaver otherSaver = mock(CompetitionSetupSectionSaver.class);
		when(otherSaver.sectionToSave()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
		when(otherSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(false);


		service.setCompetitionSetupSectionSavers(asList(matchingSaver, otherSaver));
		
		service.saveCompetitionSetupSection(competitionSetupForm, competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);
		
		verify(matchingSaver).saveSection(competitionResource, competitionSetupForm);
		verify(otherSaver, never()).saveSection(competitionResource, competitionSetupForm);
	}
	
}
