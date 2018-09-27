package org.innovateuk.ifs.competitionsetup.eligibility.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EligibilityFormPopulatorTest {

    @InjectMocks
    private EligibilityFormPopulator service;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void testSectionToFill() {
        CompetitionSetupSection result = service.sectionToFill();
        assertEquals(CompetitionSetupSection.ELIGIBILITY, result);
    }

    @Test
    public void populateForm() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .withMaximum(60)
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .withMaxResearchRatio(50)
                .withMultiStream(true)
                .withStreamName("streamname")
                .withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
                .withLeadApplicantType(asList(2L))
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.isMaximumFundingLevelOverridden(competition.getId()))
                .thenReturn(restSuccess(true));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof EligibilityForm);
        EligibilityForm form = (EligibilityForm) result;
        assertEquals(CollectionFunctions.asLinkedSet(2L, 3L), form.getResearchCategoryId());
        assertEquals("no", form.getMultipleStream());
        assertEquals(null, form.getStreamName());
        assertTrue(form.getResearchCategoriesApplicable());
        assertEquals("collaborative", form.getSingleOrCollaborative());
        assertEquals(asList(2L), form.getLeadApplicantTypes());
        assertEquals(2, form.getResearchParticipationAmountId());
        assertEquals(gcms.get(0).getMaximum(), form.getFundingLevelPercentage());
    }

    @Test
    public void populateForm_researchParticipationAmountId() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource().build(2);

        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .withMultiStream(true)
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withStreamName("streamname")
                .withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .withLeadApplicantType(asList(2L))
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.isMaximumFundingLevelOverridden(competition.getId()))
                .thenReturn(restSuccess(true));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId()))
                .thenReturn(restSuccess(gcms.get(0)));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof EligibilityForm);
        EligibilityForm form = (EligibilityForm) result;
        assertEquals(1, form.getResearchParticipationAmountId());
    }

    @Test
    public void populateForm_researchCategoriesApplicableIsFalse() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .withMaximum(60)
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .withMaxResearchRatio(50)
                .withMultiStream(true)
                .withStreamName("streamname")
                .withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
                .withLeadApplicantType(asList(2L))
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        when(grantClaimMaximumRestService.isMaximumFundingLevelOverridden(competition.getId()))
                .thenReturn(restSuccess(true));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                RESEARCH_CATEGORY)).thenReturn(restFailure(new Error(GENERAL_NOT_FOUND, HttpStatus.NOT_FOUND)));

        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof EligibilityForm);
        EligibilityForm form = (EligibilityForm) result;
        assertFalse(form.getResearchCategoriesApplicable());
    }
}
