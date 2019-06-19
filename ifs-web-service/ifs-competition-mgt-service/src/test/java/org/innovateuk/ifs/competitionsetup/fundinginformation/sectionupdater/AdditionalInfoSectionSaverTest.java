package org.innovateuk.ifs.competitionsetup.fundinginformation.sectionupdater;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.form.FunderRowForm;
import org.innovateuk.ifs.competitionsetup.fundinginformation.form.AdditionalInfoForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.Funder.ADVANCED_PROPULSION_CENTRE_APC;
import static org.innovateuk.ifs.competition.resource.Funder.AEROSPACE_TECHNOLOGY_INSTITUTE_ATI;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AdditionalInfoSectionSaverTest {

	@InjectMocks
	private AdditionalInfoSectionUpdater service;
	
	@Mock
	private CompetitionSetupRestService competitionSetupRestService;
	
	@Test
	public void saveCompetitionSetupSection() {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm("PAF", "Activity", "BudgetCode", Collections.emptyList());

		CompetitionResource competition = newCompetitionResource()
				.withId(1L).build();

		when(competitionSetupRestService.update(competition)).thenReturn(RestResult.restSuccess());

		service.saveSection(competition, competitionSetupForm);

		assertEquals("Activity", competition.getActivityCode());
		assertEquals("BudgetCode", competition.getBudgetCode());
		assertEquals("PAF", competition.getPafCode());

		verify(competitionSetupRestService).update(competition);
	}

	@Test
	public void supportsForm() {
		assertTrue(service.supportsForm(AdditionalInfoForm.class));
		assertFalse(service.supportsForm(CompetitionSetupForm.class));
	}

	@Test
	public void onlyFundersAreUpdatedAfterSetupAndLive() {
		String newPafNumber = "newPafNumber";
		String newActivityCode = "newActivityCode";
		String newBudgetCode = "newBudgetCoce";

		String oldPafCode = "oldPafCode";
		String oldActivityCode = "oldBudgetCode";
		String oldBudgetCode = "oldActivityCode";

		ZonedDateTime yesterday = ZonedDateTime.now().minusDays(1);
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);

		CompetitionFunderResource funderResource1 = newCompetitionFunderResource()
				.withFunder(ADVANCED_PROPULSION_CENTRE_APC)
				.withFunderBudget(BigInteger.valueOf(1))
				.build();
		CompetitionFunderResource funderResource2 = newCompetitionFunderResource()
				.withFunder(AEROSPACE_TECHNOLOGY_INSTITUTE_ATI)
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

		when(competitionSetupRestService.update(competition)).thenReturn(RestResult.restSuccess());

		service.saveSection(competition, competitionSetupForm);

		ArgumentCaptor<CompetitionResource> argumentCaptor = ArgumentCaptor.forClass(CompetitionResource.class);
		verify(competitionSetupRestService).update(argumentCaptor.capture());

		assertEquals(newPafNumber, 		argumentCaptor.getValue().getPafCode());
		assertEquals(newActivityCode, 	argumentCaptor.getValue().getActivityCode());
		assertEquals(newBudgetCode, 	argumentCaptor.getValue().getBudgetCode());

		CompetitionFunderResource createdFunderResource1 = argumentCaptor.getValue().getFunders().get(0);
		CompetitionFunderResource createdFunderResource2 = argumentCaptor.getValue().getFunders().get(1);

		assertEquals(funderResource1.getFunder(), createdFunderResource1.getFunder());
		assertEquals(funderResource1.getFunderBudget(), createdFunderResource1.getFunderBudget());
		assertEquals(funderResource2.getFunder(), createdFunderResource2.getFunder());
		assertEquals(funderResource2.getFunderBudget(), createdFunderResource2.getFunderBudget());

		verify(competitionSetupRestService).update(competition);
	}

	@Test
	public void updateResultsInFailureAfterCompetitionNotificationsWereSent() {
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

		verify(competitionSetupRestService, never()).update(competition);
	}
}
