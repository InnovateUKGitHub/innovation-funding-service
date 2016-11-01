package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.google.common.collect.Lists;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionFunderResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.viewmodel.FunderViewModel;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalInfoSectionSaverTest {

	@InjectMocks
	private AdditionalInfoSectionSaver service;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveCompetitionSetupSection() {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm("Activity", "Innovate", "BudgetCode", asList(new FunderViewModel()));

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

		service.saveSection(competition, competitionSetupForm);

		assertEquals("Activity", competition.getActivityCode());
		assertEquals("Innovate", competition.getInnovateBudget());
		assertEquals("BudgetCode", competition.getBudgetCode());

		verify(competitionService).update(competition);
	}

	@Test
	public void testAutoSaveFunders() {
		CompetitionResource competition = newCompetitionResource().build();
		int expectedFunders = competition.getFunders().size() + 3;
		int lastIndex = expectedFunders - 1;
		String validBudget = "199122.02";

		//Test that auto save will fill in the blank funders.
		List<Error> errors = service.autoSaveSectionField(competition,
				"funder["+ lastIndex +"].funderBudget", validBudget, Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(expectedFunders));
		assertThat(competition.getFunders().get(lastIndex).getFunderBudget(), CoreMatchers.equalTo(new BigDecimal(validBudget)));
		assertTrue(errors.isEmpty());

		//Test funder budget that is too large.
		errors = service.autoSaveSectionField(competition,
				"funder["+ lastIndex +"].funderBudget", "9123213123123123.00", Optional.empty());
		assertFalse(errors.isEmpty());

		//Test funder budget with invalid decimal places.
		errors = service.autoSaveSectionField(competition,
				"funder["+ lastIndex +"].funderBudget", "123.001", Optional.empty());
		assertFalse(errors.isEmpty());

		//Test funder budget with a negative number.
		errors = service.autoSaveSectionField(competition,
				"funder["+ lastIndex +"].funderBudget", "-1", Optional.empty());
		assertFalse(errors.isEmpty());
	}


	@Test
	public void testAutoSaveRemoveFunders() {
		CompetitionResource competition = newCompetitionResource().withFunders(Lists.newArrayList(
				new CompetitionFunderResource(),
				new CompetitionFunderResource(),
				new CompetitionFunderResource()
		)).build();

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(3));

		//Test that out of range request to remove funders will leave the competition unchanged.
		List<Error> errors = service.autoSaveSectionField(competition,
				"removeFunder", "4", Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(3));
		assertTrue(errors.isEmpty());
		
		//Test that a valid index can be removed.
		errors = service.autoSaveSectionField(competition,
				"removeFunder", "2", Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(2));
		assertTrue(errors.isEmpty());

		//Test trying to remove 0th funder will fail with error.
		errors = service.autoSaveSectionField(competition,
				"removeFunder", "0", Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(2));
		assertFalse(errors.isEmpty());
	}
}
