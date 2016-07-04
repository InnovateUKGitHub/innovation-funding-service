package com.worth.ifs.competitionsetup.service.modelpopulator;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertArrayEquals;
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
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.competition.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.competition.service.CategoryFormatter;
import com.worth.ifs.util.CollectionFunctions;

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
		
		List<CategoryResource> researchCategories = new ArrayList<>();
		when(categoryService.getCategoryByType(CategoryType.RESEARCH_CATEGORY)).thenReturn(researchCategories);
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
