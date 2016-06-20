package com.worth.ifs.service;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.controller.form.CompetitionSetupForm;
import com.worth.ifs.controller.form.CompetitionSetupInitialDetailsForm;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;

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
	
	@Test
	public void testPopulateCompetitionSectionModelAttributes() {
		List<CompetitionSetupSection> completedSections = new ArrayList<>();
		when(competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(8L)).thenReturn(completedSections);
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
		
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource().withCompetitionCode("code").withName("name").withId(8L).build();
		CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
		
		service.populateCompetitionSectionModelAttributes(model, competition, section);
		
		assertEquals(12, model.asMap().size());
		assertEquals(Boolean.TRUE, model.asMap().get("editable"));
		assertEquals(competition, model.asMap().get("competition"));
		assertEquals(section, model.asMap().get("currentSection"));
		assertEquals("section-initial", model.asMap().get("currentSectionFragment"));
		assertArrayEquals(CompetitionSetupSection.values(), (Object[])model.asMap().get("allSections"));
		assertEquals(completedSections, model.asMap().get("allCompletedSections"));
		assertEquals("code: name", model.asMap().get("subTitle"));
		assertEquals(compExecs, model.asMap().get("competitionExecutiveUsers"));
		assertEquals(innovationSectors, model.asMap().get("innovationSectors"));
		assertEquals(innovationAreas, model.asMap().get("innovationAreas"));
		assertEquals(competitionTypes, model.asMap().get("competitionTypes"));
		assertEquals(leadTechs, model.asMap().get("competitionLeadTechUsers"));
	}
	
	@Test
	public void testGetSectionFormDataInitialDetails() {
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionType(4L)
				.withExecutive(5L)
				.withInnovationArea(6L)
				.withLeadTechnologist(7L)
				.withStartDate(LocalDateTime.of(2000, 1, 2, 3, 4))
				.withCompetitionCode("code")
				.withPafCode("paf")
				.withName("name")
				.withBudgetCode("budgetcode")
				.withId(8L).build();

		CompetitionSetupForm result = service.getSectionFormData(competition, CompetitionSetupSection.INITIAL_DETAILS);
		
		assertTrue(result instanceof CompetitionSetupInitialDetailsForm);
		CompetitionSetupInitialDetailsForm form = (CompetitionSetupInitialDetailsForm) result;
		assertEquals(Long.valueOf(4L), form.getCompetitionTypeId());
		assertEquals(Long.valueOf(5L), form.getExecutiveUserId());
		assertEquals(Long.valueOf(6L), form.getInnovationAreaCategoryId());
		assertEquals(Long.valueOf(7L), form.getLeadTechnologistUserId());
		assertEquals(Integer.valueOf(2), form.getOpeningDateDay());
		assertEquals(Integer.valueOf(1), form.getOpeningDateMonth());
		assertEquals(Integer.valueOf(2000), form.getOpeningDateYear());
		assertEquals("code", form.getCompetitionCode());
		assertEquals("paf", form.getPafNumber());
		assertEquals("name", form.getTitle());
		assertEquals("budgetcode", form.getBudgetCode());
	}
	
	@Test
	public void testSaveCompetitionSetupSection() {
		CompetitionSetupInitialDetailsForm competitionSetupForm = new CompetitionSetupInitialDetailsForm();
		competitionSetupForm.setTitle("title");
		competitionSetupForm.setBudgetCode("budgetCode");
		competitionSetupForm.setExecutiveUserId(1L);
		competitionSetupForm.setOpeningDateDay(1);
		competitionSetupForm.setOpeningDateMonth(12);
		competitionSetupForm.setOpeningDateYear(2000);
		competitionSetupForm.setCompetitionTypeId(2L);
		competitionSetupForm.setLeadTechnologistUserId(3L);
		competitionSetupForm.setPafNumber("paf");
		competitionSetupForm.setInnovationAreaCategoryId(4L);
		competitionSetupForm.setInnovationSectorCategoryId(5L);
		
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("compcode").build();

		service.saveCompetitionSetupSection(competitionSetupForm, competition, CompetitionSetupSection.INITIAL_DETAILS);
		
		assertEquals("title", competition.getName());
		assertEquals("budgetCode", competition.getBudgetCode());
		assertEquals(Long.valueOf(1L), competition.getExecutive());
		assertEquals(LocalDateTime.of(2000, 12, 1, 0, 0), competition.getStartDate());
		assertEquals(Long.valueOf(2L), competition.getCompetitionType());
		assertEquals(Long.valueOf(3L), competition.getLeadTechnologist());
		assertEquals("paf", competition.getPafCode());
		assertEquals(Long.valueOf(4L), competition.getInnovationArea());
		assertEquals(Long.valueOf(5L), competition.getInnovationSector());

		verify(competitionService).update(competition);
		
		assertEquals("compcode", competitionSetupForm.getCompetitionCode());
	}
}
