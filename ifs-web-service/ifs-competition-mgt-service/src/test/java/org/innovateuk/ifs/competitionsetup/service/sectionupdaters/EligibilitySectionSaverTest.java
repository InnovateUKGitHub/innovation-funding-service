package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.EligibilityForm;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EligibilitySectionSaverTest {
	
	@InjectMocks
	private EligibilitySectionSaver service;

	@Mock
	private MilestoneRestService milestoneRestService;
	
	@Mock
	private CompetitionService competitionService;
	
	@Test
	public void testSaveCompetitionSetupSection() {
		EligibilityForm competitionSetupForm = new EligibilityForm();
		competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
		competitionSetupForm.setMultipleStream("yes");
		competitionSetupForm.setStreamName("streamname");
		competitionSetupForm.setResubmission("yes");
		competitionSetupForm.setResearchCategoryId(CollectionFunctions.asLinkedSet(1L, 2L, 3L));
		competitionSetupForm.setResearchParticipationAmountId(1);
		competitionSetupForm.setSingleOrCollaborative("collaborative");
		
		CompetitionResource competition = newCompetitionResource().build();

		service.saveSection(competition, competitionSetupForm);

		assertEquals(asList(OrganisationTypeEnum.BUSINESS.getId(), OrganisationTypeEnum.RESEARCH.getId()), competition.getLeadApplicantTypes());
		assertTrue(competition.isMultiStream());
		assertEquals("streamname", competition.getStreamName());
		assertEquals(CollectionFunctions.asLinkedSet(1L, 2L, 3L), competition.getResearchCategories());
		assertEquals(Integer.valueOf(30), competition.getMaxResearchRatio());
		assertEquals(CollaborationLevel.COLLABORATIVE, competition.getCollaborationLevel());

		verify(competitionService).update(competition);
	}

	@Test
	public void testAutoSaveResearchCategoryCheck() {
		when(milestoneRestService.getAllMilestonesByCompetitionId(1L)).thenReturn(restSuccess(singletonList(getMilestone())));
		EligibilityForm form = new EligibilityForm();
		Set<Long> researchCategories = new HashSet<>();
		researchCategories.add(33L);
		researchCategories.add(34L);


		CompetitionResource competition = newCompetitionResource().withResearchCategories(researchCategories).build();
		competition.setMilestones(singletonList(10L));
		when(competitionService.update(competition)).thenReturn(serviceSuccess());

		ServiceResult<Void> result = service.autoSaveSectionField(competition, form, "researchCategoryId", "35", null);

		assertTrue(result.isSuccess());
		verify(competitionService).update(competition);

		assertTrue(competition.getResearchCategories().contains(35L));
	}

	@Test
	public void testAutoSaveResearchCategoryUncheck() {
		when(milestoneRestService.getAllMilestonesByCompetitionId(1L)).thenReturn(restSuccess(singletonList(getMilestone())));
		EligibilityForm form = new EligibilityForm();
		Set<Long> researchCategories = new HashSet<>();
		researchCategories.add(33L);
		researchCategories.add(34L);
		researchCategories.add(35L);

		CompetitionResource competition = newCompetitionResource().withResearchCategories(researchCategories).build();
		competition.setMilestones(singletonList(10L));
		when(competitionService.update(competition)).thenReturn(serviceSuccess());

		ServiceResult<Void> result = service.autoSaveSectionField(competition, form, "researchCategoryId", "35", null);

		assertTrue(result.isSuccess());
		verify(competitionService).update(competition);

		assertTrue(!competition.getResearchCategories().contains(35L));
	}

	private MilestoneResource getMilestone(){
		MilestoneResource milestone = new MilestoneResource();
		milestone.setId(10L);
		milestone.setType(MilestoneType.OPEN_DATE);
		milestone.setDate(ZonedDateTime.of(2020, 12, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
		milestone.setCompetitionId(1L);
		return milestone;
	}

	@Test
	public void testsSupportsForm() {
		assertTrue(service.supportsForm(EligibilityForm.class));
		assertFalse(service.supportsForm(CompetitionSetupForm.class));
	}

}
