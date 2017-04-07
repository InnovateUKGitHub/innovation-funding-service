package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import com.google.common.collect.Lists;
import org.hamcrest.*;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.FunderRowForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdditionalInfoSectionSaverTest {

	@InjectMocks
	private AdditionalInfoSectionSaver service;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveCompetitionSetupSection() {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm("PAF", "Activity", "BudgetCode", Collections.emptyList());

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

		service.saveSection(competition, competitionSetupForm);

		assertEquals("Activity", competition.getActivityCode());
		assertEquals("BudgetCode", competition.getBudgetCode());
		assertEquals("PAF", competition.getPafCode());

		verify(competitionService).update(competition);
	}

	@Test
	public void testAutoSaveFunders() {
		CompetitionResource competition = newCompetitionResource().build();
		int expectedFunders = competition.getFunders().size() + 3;
		int lastIndex = expectedFunders - 1;
		String validBudget = "199122";
		AdditionalInfoForm form = new AdditionalInfoForm();
        when(competitionService.update(competition)).thenReturn(serviceSuccess());

		//Test that auto save will fill in the blank funders.
		ServiceResult<Void> result = service.autoSaveSectionField(competition, form,
				"funder["+ lastIndex +"].funderBudget", validBudget, Optional.empty());

		assertThat(competition.getFunders().size(), CoreMatchers.equalTo(expectedFunders));
		assertThat(competition.getFunders().get(lastIndex).getFunderBudget(), CoreMatchers.equalTo(new BigInteger(validBudget)));
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

		String oldPafCode = "oldPafCode";
		String oldActivityCode = "oldBudgetCode";
		String oldBudgetCode = "oldActivityCode";

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		CompetitionFunderResource funderResource1 = newCompetitionFunderResource()
				.withFunder("Funder 1")
				.withFunderBudget(BigInteger.valueOf(1))
				.build();
		CompetitionFunderResource funderResource2 = newCompetitionFunderResource()
				.withFunder("Funder 2")
				.withFunderBudget(BigInteger.valueOf(2))
				.build();

		List<FunderRowForm> newFunders = new ArrayList<>();
		newFunders.add(new FunderRowForm(funderResource1));
		newFunders.add(new FunderRowForm(funderResource2));

		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm(newPafNumber, newActivityCode, newBudgetCode, newFunders);

		CompetitionResource competition = newCompetitionResource()
				.withId(1L)
				.withPafCode(oldPafCode)
				.withBudgetCode(oldBudgetCode)
				.withActivityCode(oldActivityCode)
				.withSetupComplete(true)
				.withStartDate(yesterday)
				.withFundersPanelDate(tomorrow)
				.build();

		service.saveSection(competition, competitionSetupForm);

		ArgumentCaptor<CompetitionResource> argumentCaptor = ArgumentCaptor.forClass(CompetitionResource.class);
		verify(competitionService).update(argumentCaptor.capture());

		assertEquals(newPafNumber, 		argumentCaptor.getValue().getPafCode());
		assertEquals(newActivityCode, 	argumentCaptor.getValue().getActivityCode());
		assertEquals(newBudgetCode, 	argumentCaptor.getValue().getBudgetCode());

		CompetitionFunderResource createdFunderResource1 = argumentCaptor.getValue().getFunders().get(0);
		CompetitionFunderResource createdFunderResource2 = argumentCaptor.getValue().getFunders().get(1);

		assertEquals(funderResource1.getFunder(), createdFunderResource1.getFunder());
		assertEquals(funderResource1.getFunderBudget(), createdFunderResource1.getFunderBudget());
		assertEquals(funderResource2.getFunder(), createdFunderResource2.getFunder());
		assertEquals(funderResource2.getFunderBudget(), createdFunderResource2.getFunderBudget());

		verify(competitionService).update(competition);
	}

	@Test
	public void testUpdateResultsInFailureAfterCompetitionNotificationsWereSent() {
		String newPafNumber = "newPafNumber";
		String newActivityCode = "newActivityCode";
		String newBudgetCode = "newBudgetCoce";

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);

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
