package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competitionsetup.form.InitialDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InitialDetailsSectionSaverTest {

	@InjectMocks
	private InitialDetailsSectionSaver service;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveCompetitionSetupSection() {
		InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
		competitionSetupForm.setTitle("title");
		competitionSetupForm.setExecutiveUserId(1L);
		competitionSetupForm.setOpeningDateDay(1);
		competitionSetupForm.setOpeningDateMonth(12);
		competitionSetupForm.setOpeningDateYear(2000);
		competitionSetupForm.setCompetitionTypeId(2L);
		competitionSetupForm.setLeadTechnologistUserId(3L);
		competitionSetupForm.setInnovationAreaCategoryId(4L);
		competitionSetupForm.setInnovationSectorCategoryId(5L);
		
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("compcode").build();

		service.saveSection(competition, competitionSetupForm);
		
		assertEquals("title", competition.getName());
		assertEquals(Long.valueOf(1L), competition.getExecutive());
		assertEquals(LocalDateTime.of(2000, 12, 1, 0, 0), competition.getStartDate());
		assertEquals(Long.valueOf(2L), competition.getCompetitionType());
		assertEquals(Long.valueOf(3L), competition.getLeadTechnologist());
		assertEquals(Long.valueOf(4L), competition.getInnovationArea());
		assertEquals(Long.valueOf(5L), competition.getInnovationSector());

		verify(competitionService).update(competition);

	}
}
