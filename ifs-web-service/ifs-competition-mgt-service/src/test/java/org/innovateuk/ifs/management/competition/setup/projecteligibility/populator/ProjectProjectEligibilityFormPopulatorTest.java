package org.innovateuk.ifs.management.competition.setup.projecteligibility.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.form.ProjectEligibilityForm;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectProjectEligibilityFormPopulatorTest {

    @InjectMocks
    private ProjectEligibilityFormPopulator service;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void testSectionToFill() {
        CompetitionSetupSection result = service.sectionToFill();
        assertEquals(CompetitionSetupSection.PROJECT_ELIGIBILITY, result);
    }

    @Test
    public void populateForm() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .withMaximum(60)
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withMaxResearchRatio(50)
                .withMultiStream(true)
                .withStreamName("streamname")
                .withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
                .withLeadApplicantType(asList(2L))
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .withFundingType(FundingType.GRANT)
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()))
                .thenReturn(restSuccess(true));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof ProjectEligibilityForm);
        ProjectEligibilityForm form = (ProjectEligibilityForm) result;
        assertEquals("no", form.getMultipleStream());
        assertEquals(null, form.getStreamName());
        assertEquals("collaborative", form.getSingleOrCollaborative());
        assertFalse(form.isKtpCompetition());
        assertEquals(asList(2L), form.getLeadApplicantTypes());
        assertEquals(2, form.getResearchParticipationAmountId());
    }

    @Test
    public void populateForm_Ktp() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .withMaximum(60)
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .withMaxResearchRatio(50)
                .withMultiStream(true)
                .withStreamName("streamname")
                .withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
                .withLeadApplicantType(Collections.emptyList())
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .withFundingType(FundingType.KTP)
                .build();

        QuestionResource researchCategoryQuestion = newQuestionResource().build();

        when(grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()))
                .thenReturn(restSuccess(true));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof ProjectEligibilityForm);
        ProjectEligibilityForm form = (ProjectEligibilityForm) result;
        assertEquals("no", form.getMultipleStream());
        assertEquals(null, form.getStreamName());
        assertEquals("collaborative", form.getSingleOrCollaborative());
        assertTrue(form.isKtpCompetition());
        assertEquals(0, form.getLeadApplicantTypes().size());
        assertEquals(2, form.getResearchParticipationAmountId());
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

        when(grantClaimMaximumRestService.isMaximumFundingLevelConstant(competition.getId()))
                .thenReturn(restSuccess(true));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                RESEARCH_CATEGORY)).thenReturn(restSuccess(researchCategoryQuestion));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId()))
                .thenReturn(restSuccess(gcms.get(0)));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof ProjectEligibilityForm);
        ProjectEligibilityForm form = (ProjectEligibilityForm) result;
        assertEquals(1, form.getResearchParticipationAmountId());
    }
}
