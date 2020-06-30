package org.innovateuk.ifs.management.competition.setup.projecteligibility.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.form.ProjectEligibilityForm;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectEligibilitySectionSaverTest {

    @InjectMocks
    private ProjectEligibilitySectionUpdater service;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Test
    public void saveSection() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setSingleOrCollaborative("collaborative");
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setResearchCategoryId(asLinkedSet(1L, 2L, 3L));
        competitionSetupForm.setOverrideFundingRules(true);
        competitionSetupForm.setFundingLevelPercentageOverride(10);
        competitionSetupForm.setMultipleStream("yes");
        competitionSetupForm.setStreamName("streamname");
        competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.THIRTY.getId());
        competitionSetupForm.setResubmission("yes");

        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource().build(2);

        CompetitionResource competition = newCompetitionResource()
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .withApplicationFinanceType(STANDARD)
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(1).getId())).thenReturn(restSuccess(gcms.get(1)));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.save(any())).thenReturn(restSuccess(gcms.get(0)));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        assertEquals(asList(BUSINESS.getId(), OrganisationTypeEnum.RESEARCH.getId()), competition.getLeadApplicantTypes());
        assertTrue(competition.isMultiStream());
        assertEquals("streamname", competition.getStreamName());
        assertEquals(asLinkedSet(1L, 2L, 3L), competition.getResearchCategories());
        assertEquals(ResearchParticipationAmount.THIRTY.getAmount(), competition.getMaxResearchRatio());
        assertEquals(CollaborationLevel.COLLABORATIVE, competition.getCollaborationLevel());

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_researchCategoriesApplicableIsFalseAndQuestionExists() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setSingleOrCollaborative("single");
        competitionSetupForm.setResearchCategoriesApplicable(false);
        competitionSetupForm.setOverrideFundingRules(false);
        competitionSetupForm.setMultipleStream("no");
        competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.THIRTY.getId());
        competitionSetupForm.setResubmission("no");

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withGrantClaimMaximums(asLinkedSet(98L, 99L))
                .withApplicationFinanceType(STANDARD)
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumsForCompetitionType(competition.getCompetitionType()))
                .thenReturn(restSuccess(asLinkedSet(1L, 2L)));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(questionSetupCompetitionRestService.deleteById(anyLong())).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService, only()).update(competition);
        verify(questionSetupCompetitionRestService).deleteById(isA(Long.class));
        verify(questionSetupCompetitionRestService, never()).addResearchCategoryQuestionToCompetition(isA(Long.class));

    }

    @Test
    public void saveSection_researchCategoriesApplicableIsFalseAndQuestionIsAbsent() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setSingleOrCollaborative("single");
        competitionSetupForm.setResearchCategoriesApplicable(false);
        competitionSetupForm.setOverrideFundingRules(false);
        competitionSetupForm.setMultipleStream("no");
        competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.THIRTY.getId());
        competitionSetupForm.setResubmission("no");

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withGrantClaimMaximums(asLinkedSet(98L, 99L))
                .withApplicationFinanceType(STANDARD)
                .build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumsForCompetitionType(competition.getCompetitionType()))
                .thenReturn(restSuccess(asLinkedSet(1L, 2L)));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restFailure(GENERAL_NOT_FOUND));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService).update(competition);
        verify(questionSetupCompetitionRestService, never()).deleteById(isA(Long.class));
        verify(questionSetupCompetitionRestService, never()).addResearchCategoryQuestionToCompetition(isA(Long.class));
    }

    @Test
    public void saveSection_researchCategoriesApplicableIsTrueAndQuestionExists() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setSingleOrCollaborative("single");
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setOverrideFundingRules(false);
        competitionSetupForm.setMultipleStream("no");
        competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.THIRTY.getId());
        competitionSetupForm.setResubmission("no");

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withGrantClaimMaximums(asLinkedSet(98L, 99L))
                .withApplicationFinanceType(STANDARD)
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumsForCompetitionType(competition.getCompetitionType()))
                .thenReturn(restSuccess(asLinkedSet(1L, 2L)));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService).update(competition);
        verify(questionSetupCompetitionRestService, never()).deleteById(isA(Long.class));
        verify(questionSetupCompetitionRestService, never()).addResearchCategoryQuestionToCompetition(isA(Long.class));
    }

    @Test
    public void saveSection_researchCategoriesApplicableIsTrueAndQuestionIsAbsent() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setSingleOrCollaborative("single");
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setOverrideFundingRules(false);
        competitionSetupForm.setMultipleStream("no");
        competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.THIRTY.getId());
        competitionSetupForm.setResubmission("no");

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withGrantClaimMaximums(asLinkedSet(98L, 99L))
                .withApplicationFinanceType(STANDARD)
                .build();

        when(grantClaimMaximumRestService.getGrantClaimMaximumsForCompetitionType(competition.getCompetitionType()))
                .thenReturn(restSuccess(asLinkedSet(1L, 2L)));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restFailure(GENERAL_NOT_FOUND));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_withoutResearchParticipationAmountIdDefaultsToNone() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setOverrideFundingRules(true);
        competitionSetupForm.setFundingLevelPercentageOverride(50);
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(STANDARD)
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(1).getId())).thenReturn(restSuccess(gcms.get(1)));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.save(any())).thenReturn(restSuccess(gcms.get(0)));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        assertEquals(ResearchParticipationAmount.NONE.getAmount(), competition.getMaxResearchRatio());

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_withoutResearchParticipationAndFundingLevels() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setResearchCategoriesApplicable(false);
        competitionSetupForm.setOverrideFundingRules(false);
        competitionSetupForm.setFundingLevelPercentage(50);
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(STANDARD)
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(1).getId())).thenReturn(restSuccess(gcms.get(1)));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.save(any())).thenReturn(restSuccess(gcms.get(0)));
        when(questionSetupCompetitionRestService.deleteById(researchCategoryQuestion.getId())).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_defaultsMaxResearchRatioToNoneForCompetitionsWithNoFinances() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.HUNDRED.getId());
        competitionSetupForm.setOverrideFundingRules(true);
        competitionSetupForm.setFundingLevelPercentageOverride(50);

        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource().build(2);

        CompetitionResource competition = newCompetitionResource()
                .withNonFinanceType(true)
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(1).getId())).thenReturn(restSuccess(gcms.get(1)));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.save(any())).thenReturn(restSuccess(gcms.get(0)));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        assertEquals(0, competition.getMaxResearchRatio().intValue());

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_withoutOverriddenFundingRules() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setResearchParticipationAmountId(ResearchParticipationAmount.HUNDRED.getId());
        competitionSetupForm.setOverrideFundingRules(false);

        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource().build(2);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(BUSINESS.getId())
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.getGrantClaimMaximumsForCompetitionType(competition.getCompetitionType()))
                .thenReturn(restSuccess(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId())));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService).update(competition);

        assertEquals(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()), competition.getGrantClaimMaximums());
    }

    @Test
    public void supportsForm() {
        assertTrue(service.supportsForm(ProjectEligibilityForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }
}
