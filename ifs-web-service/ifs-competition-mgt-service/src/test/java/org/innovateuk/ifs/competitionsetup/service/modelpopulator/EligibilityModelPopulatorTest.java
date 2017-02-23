package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.LeadApplicantType;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.service.CategoryFormatter;
import org.innovateuk.ifs.util.CollectionFunctions;

@RunWith(MockitoJUnitRunner.class)
public class EligibilityModelPopulatorTest {

	@InjectMocks
	private EligibilityModelPopulator populator;
	
	@Mock
	private CategoryService categoryService;
	
	@Mock
	private CategoryFormatter categoryFormatter;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSection.ELIGIBILITY, result);
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
		
		List<ResearchCategoryResource> researchCategories = new ArrayList<>();
		when(categoryService.getResearchCategories()).thenReturn(researchCategories);
		when(categoryFormatter.format(CollectionFunctions.asLinkedSet(2L, 3L), researchCategories)).thenReturn("formattedcategories");

		populator.populateModel(model, competition);
		
		assertEquals(5, model.asMap().size());
		assertArrayEquals(ResearchParticipationAmount.values(), (Object[])model.asMap().get("researchParticipationAmounts"));
		assertArrayEquals(CollaborationLevel.values(), (Object[])model.asMap().get("collaborationLevels"));
		assertArrayEquals(LeadApplicantType.values(), (Object[])model.asMap().get("leadApplicantTypes"));
		assertEquals(researchCategories, model.asMap().get("researchCategories"));
		assertEquals("formattedcategories", model.asMap().get("researchCategoriesFormatted"));
	}
}
