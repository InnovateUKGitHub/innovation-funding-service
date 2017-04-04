package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CategoryFormatter;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InitialDetailsFormPopulatorTest {

	@InjectMocks
	private InitialDetailsFormPopulator service;

	@Mock
	private CategoryService categoryService;

	@Mock
	private CategoryFormatter categoryFormatter;
	
	@Test
	public void testSectionToFill() {
		CompetitionSetupSection result = service.sectionToFill();
		assertEquals(CompetitionSetupSection.INITIAL_DETAILS, result);
	}
				
	@Test
	public void testGetSectionFormDataInitialDetails() {

		Set<Long> innovationAreas = Stream.of(6L, 66L).collect(Collectors.toSet());

		CompetitionResource competition = newCompetitionResource()
				.withCompetitionType(4L)
				.withExecutive(5L)
				.withInnovationAreas(innovationAreas)
				.withLeadTechnologist(7L)
				.withStartDate(ZonedDateTime.of(2000, 1, 2, 3, 4, 0, 0, ZoneId.systemDefault()))
				.withCompetitionCode("code")
				.withPafCode("paf")
				.withName("name")
				.withBudgetCode("budgetcode")
				.withId(8L).build();

		List<InnovationAreaResource> innovationAreaCategories = new ArrayList<>();
		when(categoryService.getInnovationAreas()).thenReturn(innovationAreaCategories);
		when(categoryFormatter.format(innovationAreas, innovationAreaCategories)).thenReturn("formattedcategories");


		CompetitionSetupForm result = service.populateForm(competition);
		
		assertTrue(result instanceof InitialDetailsForm);
		InitialDetailsForm form = (InitialDetailsForm) result;
		assertEquals(Long.valueOf(4L), form.getCompetitionTypeId());
		assertEquals(Long.valueOf(5L), form.getExecutiveUserId());
		assertThat(form.getInnovationAreaCategoryIds(), hasItems(6L, 66L));
		assertThat(form.getInnovationAreaCategoryIds(), hasSize(2));
		assertEquals("formattedcategories", form.getInnovationAreaNamesFormatted());
		assertEquals(Long.valueOf(7L), form.getLeadTechnologistUserId());
		assertEquals(Integer.valueOf(2), form.getOpeningDateDay());
		assertEquals(Integer.valueOf(1), form.getOpeningDateMonth());
		assertEquals(Integer.valueOf(2000), form.getOpeningDateYear());
		assertEquals("name", form.getTitle());
	}
}
