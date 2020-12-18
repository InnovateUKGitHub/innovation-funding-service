package org.innovateuk.ifs.management.competition.setup.fundingeligibility.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.management.competition.setup.fundingeligibility.form.FundingEligibilityResearchCategoryForm;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;

import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FundingEligibilitySectionUpdaterTest {

    @InjectMocks
    private FundingEligibilitySectionUpdater service;

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
        FundingEligibilityResearchCategoryForm competitionSetupForm = new FundingEligibilityResearchCategoryForm();
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setResearchCategoryId(newHashSet(1L, 2L, 3L));

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withResearchCategories(new HashSet<>())
                .withNonFinanceType(false)
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.revertToDefaultForCompetitionType(competition.getId()))
                .thenReturn(restSuccess(asLinkedSet(1L, 2L)));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService, only()).update(competition);
        verify(grantClaimMaximumRestService).revertToDefaultForCompetitionType(competition.getId());
        assertEquals(asLinkedSet(1L, 2L, 3L), competition.getResearchCategories());

    }
    @Test
    public void saveSection_researchCategoriesApplicableIsFalseAndQuestionExists() {
        FundingEligibilityResearchCategoryForm competitionSetupForm = new FundingEligibilityResearchCategoryForm();
        competitionSetupForm.setResearchCategoriesApplicable(false);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withResearchCategories(new HashSet<>())
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.revertToDefaultForCompetitionType(competition.getId()))
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
        FundingEligibilityResearchCategoryForm competitionSetupForm = new FundingEligibilityResearchCategoryForm();
        competitionSetupForm.setResearchCategoriesApplicable(false);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withResearchCategories(new HashSet<>())
                .build();

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restFailure(GENERAL_NOT_FOUND));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(questionSetupCompetitionRestService, never()).deleteById(isA(Long.class));
        verify(questionSetupCompetitionRestService, never()).addResearchCategoryQuestionToCompetition(isA(Long.class));
    }

    @Test
    public void saveSection_researchCategoriesApplicableIsTrueAndQuestionExists() {
        FundingEligibilityResearchCategoryForm competitionSetupForm = new FundingEligibilityResearchCategoryForm();
        competitionSetupForm.setResearchCategoriesApplicable(true);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withResearchCategories(new HashSet<>())
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(questionSetupCompetitionRestService, never()).deleteById(isA(Long.class));
        verify(questionSetupCompetitionRestService, never()).addResearchCategoryQuestionToCompetition(isA(Long.class));
    }

    @Test
    public void saveSection_researchCategoriesApplicableIsTrueAndQuestionIsAbsent() {
        FundingEligibilityResearchCategoryForm competitionSetupForm = new FundingEligibilityResearchCategoryForm();
        competitionSetupForm.setResearchCategoriesApplicable(true);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withResearchCategories(new HashSet<>())
                .build();

        when(grantClaimMaximumRestService.revertToDefaultForCompetitionType(competition.getId()))
                .thenReturn(restSuccess(asLinkedSet(1L, 2L)));
        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restFailure(GENERAL_NOT_FOUND));
        when(questionSetupCompetitionRestService.addResearchCategoryQuestionToCompetition(competition.getId())).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService).update(competition);
        verify(questionSetupCompetitionRestService).addResearchCategoryQuestionToCompetition(competition.getId());
    }

    @Test
    public void saveSection_wontResetFundingLevelOrSaveCompNoChange() {
        FundingEligibilityResearchCategoryForm competitionSetupForm = new FundingEligibilityResearchCategoryForm();
        competitionSetupForm.setResearchCategoriesApplicable(true);
        competitionSetupForm.setResearchCategoryId(newHashSet(1L, 2L, 3L));

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionType(1L)
                .withResearchCategories(newHashSet(1L, 2L, 3L))
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));

        service.saveSection(competition, competitionSetupForm).getSuccess();

        verify(competitionSetupRestService, never()).update(competition);
        verify(grantClaimMaximumRestService, never()).revertToDefaultForCompetitionType(competition.getId());
    }

}
