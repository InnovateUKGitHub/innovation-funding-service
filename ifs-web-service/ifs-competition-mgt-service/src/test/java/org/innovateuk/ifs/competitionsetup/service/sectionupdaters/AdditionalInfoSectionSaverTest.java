package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import com.google.common.collect.Lists;
import org.hamcrest.CoreMatchers;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.viewmodel.FunderViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalInfoSectionSaverTest {

	@InjectMocks
	private AdditionalInfoSectionSaver service;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveCompetitionSetupSection() {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm("Activity", "Innovate", "BudgetCode", Collections.emptyList());

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
		AdditionalInfoForm form = new AdditionalInfoForm();
        when(competitionService.update(competition)).thenReturn(serviceSuccess());

		//Test that auto save will fill in the blank funders.
		ServiceResult<Void> result = service.autoSaveSectionField(competition, form,
				"funder["+ lastIndex +"].funderBudget", validBudget, Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(expectedFunders));
		assertThat(competition.getFunders().get(lastIndex).getFunderBudget(), CoreMatchers.equalTo(new BigDecimal(validBudget)));
		assertTrue(result.isSuccess());

	}


	@Test
	public void testAutoSaveRemoveFunders() {
		CompetitionResource competition = newCompetitionResource().withFunders(Lists.newArrayList(
				new CompetitionFunderResource(),
				new CompetitionFunderResource(),
				new CompetitionFunderResource()
		)).build();
		AdditionalInfoForm form = new AdditionalInfoForm();

		when(competitionService.update(competition)).thenReturn(serviceSuccess());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(3));

		//Test that out of range request to remove funders will leave the competition unchanged.
		ServiceResult<Void> result = service.autoSaveSectionField(competition, form,
				"removeFunder", "4", Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(3));
		assertTrue(result.isSuccess());

		//Test that a valid index can be removed.
		result = service.autoSaveSectionField(competition, form,
				"removeFunder", "2", Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(2));
		assertTrue(result.isSuccess());

		//Test trying to remove 0th funder will fail with error.
		result = service.autoSaveSectionField(competition, form,
				"removeFunder", "0", Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(2));
		assertFalse(result.isSuccess());
	}

	@Test
	public void testsSupportsForm() {
		assertTrue(service.supportsForm(AdditionalInfoForm.class));
		assertFalse(service.supportsForm(CompetitionSetupForm.class));
	}

	@Test
	public void testOnlyFundersAreUpdatedAfterSetupAndLive() {
		String newPafNumber = "newPafNumber";
		String newActivityCode = "newActivityCode";
		String newBudgetCode = "newBudgetCoce";

		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

		CompetitionFunderResource funderResource1 = newCompetitionFunderResource()
				.withFunder("Funder 1")
				.withFunderBudget(new BigDecimal(1))
				.build();
		CompetitionFunderResource funderResource2 = newCompetitionFunderResource()
				.withFunder("Funder 2")
				.withFunderBudget(new BigDecimal(2))
				.build();

		List<FunderViewModel> newFunders = new ArrayList<>();
		newFunders.add(new FunderViewModel(funderResource1));
		newFunders.add(new FunderViewModel(funderResource2));

		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm(newPafNumber, newActivityCode, newBudgetCode, newFunders);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L)
				.withPafCode("oldPafCode")
				.withBudgetCode("oldBudgetCode")
				.withActivityCode("oldActivityCode")
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(tomorrow)
				.build();

		service.saveSection(competition, competitionSetupForm);

		ArgumentCaptor<CompetitionResource> argumentCaptor = ArgumentCaptor.forClass(CompetitionResource.class);
		verify(competitionService).update(argumentCaptor.capture());

		assertEquals(argumentCaptor.getValue().getPafCode(), competition.getPafCode());
		assertEquals(argumentCaptor.getValue().getActivityCode(), competition.getActivityCode());
		assertEquals(argumentCaptor.getValue().getBudgetCode(), competition.getBudgetCode());

		CompetitionFunderResource createdFunderResource1 = argumentCaptor.getValue().getFunders().get(0);
		CompetitionFunderResource createdFunderResource2 = argumentCaptor.getValue().getFunders().get(1);

		assertEquals(createdFunderResource1.getFunder(), funderResource1.getFunder());
		assertEquals(createdFunderResource1.getFunderBudget(), funderResource1.getFunderBudget());
		assertEquals(createdFunderResource2.getFunder(), funderResource2.getFunder());
		assertEquals(createdFunderResource2.getFunderBudget(), funderResource2.getFunderBudget());

		verify(competitionService).update(competition);
	}

	@Test
	public void testUpdateResultsInFailureAfterCompetitionNotificationsWereSent() {
		String newPafNumber = "newPafNumber";
		String newActivityCode = "newActivityCode";
		String newBudgetCode = "newBudgetCoce";

		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm(newPafNumber, newActivityCode, newBudgetCode, Collections.emptyList());

		CompetitionResource competition = newCompetitionResource()
				.withId(1L)
				.withPafCode()
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(yesterday)
				.build();

		assertTrue(service.saveSection(competition, competitionSetupForm).isFailure());

		verify(competitionService, never()).update(competition);
	}
}
