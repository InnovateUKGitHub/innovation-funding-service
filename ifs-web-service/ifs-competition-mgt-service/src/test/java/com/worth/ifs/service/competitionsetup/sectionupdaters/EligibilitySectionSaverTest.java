package com.worth.ifs.service.competitionsetup.sectionupdaters;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.controller.form.competitionsetup.EligibilityForm;
import com.worth.ifs.util.CollectionFunctions;

@RunWith(MockitoJUnitRunner.class)
public class EligibilitySectionSaverTest {
	
	@InjectMocks
	private EligibilitySectionSaver service;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveCompetitionSetupSection() {
		EligibilityForm competitionSetupForm = new EligibilityForm();
		competitionSetupForm.setLeadApplicantType("business");
		competitionSetupForm.setMultipleStream("yes");
		competitionSetupForm.setStreamName("streamname");
		competitionSetupForm.setResearchCategoryId(CollectionFunctions.asLinkedSet(1L, 2L, 3L));
		competitionSetupForm.setResearchParticipationAmountId(1);
		competitionSetupForm.setSingleOrCollaborative("collaborative");
		
		CompetitionResource competition = newCompetitionResource().build();

		service.saveSection(competition, competitionSetupForm);
		
		assertEquals(LeadApplicantType.BUSINESS, competition.getLeadApplicantType());
		assertTrue(competition.isMultiStream());
		assertEquals("streamname", competition.getStreamName());
		assertEquals(CollectionFunctions.asLinkedSet(1L, 2L, 3L), competition.getResearchCategories());
		assertEquals(Integer.valueOf(30), competition.getMaxResearchRatio());
		assertEquals(CollaborationLevel.COLLABORATIVE, competition.getCollaborationLevel());

		verify(competitionService).update(competition);
	}
}
