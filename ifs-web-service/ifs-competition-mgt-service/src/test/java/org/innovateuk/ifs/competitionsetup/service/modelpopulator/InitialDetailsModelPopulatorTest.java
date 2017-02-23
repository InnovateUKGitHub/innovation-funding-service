package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CollectionFunctions;

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
		when(userService.findUserByType(UserRoleType.COMP_ADMIN)).thenReturn(compExecs);
		List<InnovationSectorResource> innovationSectors = new ArrayList<>();
		when(categoryService.getInnovationSectors()).thenReturn(innovationSectors);
		List<InnovationAreaResource> innovationAreas = new ArrayList<>();
		when(categoryService.getInnovationAreas()).thenReturn(innovationAreas);
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
