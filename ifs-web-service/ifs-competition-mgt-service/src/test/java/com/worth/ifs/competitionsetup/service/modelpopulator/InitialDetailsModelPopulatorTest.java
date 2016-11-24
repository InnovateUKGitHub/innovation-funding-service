package com.worth.ifs.competitionsetup.service.modelpopulator;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
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
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;
import com.worth.ifs.util.CollectionFunctions;

@RunWith(MockitoJUnitRunner.class)
public class InitialDetailsModelPopulatorTest {

	@InjectMocks
	private InitialDetailsModelPopulator populator;
	
	@Mock
	private CategoryService categoryService;
	
	@Mock
	private CompetitionService competitionService;

	@Mock
	private UserService userService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSection.INITIAL_DETAILS, result);
	}
	
	@Test
	public void testPopulateModel() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.build();
		
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
		
		populator.populateModel(model, competition);
		
		assertEquals(5, model.asMap().size());
		assertEquals(compExecs, model.asMap().get("competitionExecutiveUsers"));
		assertEquals(innovationSectors, model.asMap().get("innovationSectors"));
		assertEquals(innovationAreas, model.asMap().get("innovationAreas"));
		assertEquals(competitionTypes, model.asMap().get("competitionTypes"));
		assertEquals(leadTechs, model.asMap().get("competitionLeadTechUsers"));
	}
}
