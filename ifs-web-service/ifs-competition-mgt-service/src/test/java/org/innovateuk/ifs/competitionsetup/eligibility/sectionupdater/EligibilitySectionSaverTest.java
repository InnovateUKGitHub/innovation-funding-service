package org.innovateuk.ifs.competitionsetup.eligibility.sectionupdater;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EligibilitySectionSaverTest {
	
	@InjectMocks
	private EligibilitySectionUpdater service;

	@Mock
	private MilestoneRestService milestoneRestService;
	
	@Mock
	private CompetitionSetupRestService competitionSetupRestService;
	
	@Test
	public void saveSection() {
		EligibilityForm competitionSetupForm = new EligibilityForm();
		competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
		competitionSetupForm.setMultipleStream("yes");
		competitionSetupForm.setStreamName("streamname");
		competitionSetupForm.setResubmission("yes");
		competitionSetupForm.setResearchCategoryId(CollectionFunctions.asLinkedSet(1L, 2L, 3L));
		competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.THIRTY.getId());
		competitionSetupForm.setSingleOrCollaborative("collaborative");
		
		CompetitionResource competition = newCompetitionResource()
                .withFullApplicationFinance(true)
                .build();

		when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

		service.saveSection(competition, competitionSetupForm);

		assertEquals(asList(OrganisationTypeEnum.BUSINESS.getId(), OrganisationTypeEnum.RESEARCH.getId()), competition.getLeadApplicantTypes());
		assertTrue(competition.isMultiStream());
		assertEquals("streamname", competition.getStreamName());
		assertEquals(CollectionFunctions.asLinkedSet(1L, 2L, 3L), competition.getResearchCategories());
		assertEquals(ResearchParticipationAmount.THIRTY.getAmount(), competition.getMaxResearchRatio());
		assertEquals(CollaborationLevel.COLLABORATIVE, competition.getCollaborationLevel());

		verify(competitionSetupRestService).update(competition);
	}

    @Test
    public void saveSection_withoutResearchParticipationAmountIdDefaultsToNone() {
        CompetitionResource competition = newCompetitionResource()
                .withFullApplicationFinance(true)
                .build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        service.saveSection(competition, new EligibilityForm());

        assertEquals(ResearchParticipationAmount.NONE.getAmount(), competition.getMaxResearchRatio());

        verify(competitionSetupRestService).update(competition);
	}

    @Test
    public void saveSection_defaultsMaxResearchRatioToNoneForCompetitionsWithNullFullApplicationFinance() {
        EligibilityForm  competitionSetupForm = new EligibilityForm();
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.HUNDRED.getId());

        CompetitionResource competition = newCompetitionResource()
                .withFullApplicationFinance(null)
                .build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm);

        assertEquals(0, competition.getMaxResearchRatio().intValue());

        verify(competitionSetupRestService).update(competition);
    }

    @Test
	public void autoSaveResearchCategoryCheck() {
		when(milestoneRestService.getAllMilestonesByCompetitionId(1L)).thenReturn(restSuccess(singletonList(getMilestone())));
		EligibilityForm form = new EligibilityForm();
		Set<Long> researchCategories = Sets.newHashSet(33L, 34L);

		CompetitionResource competition = newCompetitionResource().withResearchCategories(researchCategories).build();
		competition.setMilestones(singletonList(10L));
		when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

		ServiceResult<Void> result = service.autoSaveSectionField(competition, form, "researchCategoryId", "35", null);

		assertTrue(result.isSuccess());
		verify(competitionSetupRestService).update(competition);

		assertTrue(competition.getResearchCategories().contains(35L));
	}

	@Test
	public void autoSaveResearchCategoryUncheck() {
		when(milestoneRestService.getAllMilestonesByCompetitionId(1L)).thenReturn(restSuccess(singletonList(getMilestone())));
		EligibilityForm form = new EligibilityForm();
		Set<Long> researchCategories = newHashSet(33L, 34L, 35L);

		CompetitionResource competition = newCompetitionResource().withResearchCategories(researchCategories).build();
		competition.setMilestones(singletonList(10L));
		when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

		ServiceResult<Void> result = service.autoSaveSectionField(competition, form, "researchCategoryId", "35", null);

		assertTrue(result.isSuccess());
		verify(competitionSetupRestService).update(competition);

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
	public void supportsForm() {
		assertTrue(service.supportsForm(EligibilityForm.class));
		assertFalse(service.supportsForm(CompetitionSetupForm.class));
	}
}
