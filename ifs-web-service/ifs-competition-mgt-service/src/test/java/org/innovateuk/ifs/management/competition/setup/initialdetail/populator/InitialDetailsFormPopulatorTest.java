package org.innovateuk.ifs.management.competition.setup.initialdetail.populator;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.management.competition.setup.service.CategoryFormatter;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InitialDetailsFormPopulatorTest {

	@InjectMocks
	private InitialDetailsFormPopulator service;

	@Mock
	private CategoryRestService categoryRestService;

	@Mock
	private CategoryFormatter categoryFormatter;
	
	@Test
	public void testSectionToFill() {
		CompetitionSetupSection result = service.sectionToFill();
		assertEquals(CompetitionSetupSection.INITIAL_DETAILS, result);
	}
				
	@Test
	public void getSectionFormDataInitialDetails() {
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
                .withFundingRules(FundingRules.STATE_AID)
				.withFundingType(FundingType.GRANT)
				.withId(8L).build();

		List<InnovationAreaResource> innovationAreaCategories = new ArrayList<>();
		when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaCategories));
		when(categoryFormatter.format(innovationAreas, innovationAreaCategories)).thenReturn("formattedcategories");

		CompetitionSetupForm result = service.populateForm(competition);
		
		assertTrue(result instanceof InitialDetailsForm);
		InitialDetailsForm form = (InitialDetailsForm) result;
		assertEquals(Long.valueOf(4L), form.getCompetitionTypeId());
		assertEquals(Long.valueOf(5L), form.getExecutiveUserId());
		assertThat(form.getInnovationAreaCategoryIds(), hasItems(6L, 66L));
		assertThat(form.getInnovationAreaCategoryIds(), hasSize(2));
		assertEquals("formattedcategories", form.getInnovationAreaNamesFormatted());
		assertEquals(Long.valueOf(7L), form.getInnovationLeadUserId());
		assertEquals(Integer.valueOf(2), form.getOpeningDateDay());
		assertEquals(Integer.valueOf(1), form.getOpeningDateMonth());
		assertEquals(Integer.valueOf(2000), form.getOpeningDateYear());
		assertEquals("name", form.getTitle());
		assertEquals(FundingRules.STATE_AID, form.getFundingRule());
		assertEquals(FundingType.GRANT, form.getFundingType());
	}

	@Test
	public void getSectionFormDataInitialDetailsWithAllInnovationAreas() {
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionType(4L)
				.withExecutive(5L)
				.withInnovationAreas(ImmutableSet.of(6L, 7L))
				.withLeadTechnologist(7L)
				.withStartDate(ZonedDateTime.of(2000, 1, 2, 3, 4, 0, 0, ZoneId.systemDefault()))
				.withCompetitionCode("code")
				.withPafCode("paf")
				.withName("name")
				.withBudgetCode("budgetcode")
                .withFundingRules(FundingRules.STATE_AID)
				.withId(8L).build();

		List<InnovationAreaResource> innovationAreaCategories = newInnovationAreaResource().withId(-1L, 6L, 7L).withName("None", "Innovation area 1", "Innovation area 2").build(3);
		when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaCategories));

		CompetitionSetupForm result = service.populateForm(competition);

		assertTrue(result instanceof InitialDetailsForm);
		InitialDetailsForm form = (InitialDetailsForm) result;
		assertThat(form.getInnovationAreaCategoryIds(), hasSize(1));
		assertEquals("All", form.getInnovationAreaNamesFormatted());
	}
}