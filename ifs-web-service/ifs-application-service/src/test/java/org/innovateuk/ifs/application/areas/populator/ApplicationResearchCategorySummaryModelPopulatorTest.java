package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplicationResearchCategorySummaryModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationResearchCategorySummaryModelPopulator populator;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private ApplicantRestService applicantRestService;

    @Test
    public void populateSummaryForLeadApplicant() {
        Long loggedInUser = 1L;
        Long competitionId = 2L;

        ResearchCategoryResource researchCategory = newResearchCategoryResource().withName("categoryName").build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competitionId)
                .withResearchCategory(researchCategory).build();
        QuestionResource question = newQuestionResource()
                .withCompetition(competitionId).build();
        ApplicantResource applicant = newApplicantResource()
                .withOrganisation(newOrganisationResource().build()).build();
        boolean userIsLeadApplicant = true;

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY)).thenReturn(restSuccess(question));
            when(applicantRestService.getQuestion(loggedInUser, application.getId(), question.getId()))
                .thenReturn(newApplicantQuestionResource()
                        .withCurrentApplicant(applicant)
                        .withQuestion(question)
                        .withApplicantQuestionStatuses(newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());

        ResearchCategorySummaryViewModel viewModel = populator.populate(application, loggedInUser, userIsLeadApplicant);

        assertTrue(viewModel.isClosed());
        assertTrue(viewModel.isComplete());
        assertTrue(viewModel.isCanMarkAsComplete());
        assertTrue(viewModel.isAllReadOnly());
        assertTrue(viewModel.isSummary());
        assertEquals(viewModel.getResearchCategory(), researchCategory.getName());
    }

    @Test
    public void populateSummaryForCollaborator() {
        Long loggedInUser = 1L;
        Long competitionId = 2L;

        ResearchCategoryResource researchCategory = newResearchCategoryResource().withName("categoryName").build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competitionId)
                .withResearchCategory(researchCategory).build();
        QuestionResource question = newQuestionResource()
                .withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().build();
        ApplicantResource collaborator = newApplicantResource()
                .withOrganisation(organisation).build();
        boolean userIsLeadApplicant = false;

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY)).thenReturn(restSuccess(question));
        when(applicantRestService.getQuestion(loggedInUser, application.getId(), question.getId()))
                .thenReturn(newApplicantQuestionResource()
                        .withCurrentApplicant(collaborator)
                        .withQuestion(question)
                        .withApplicantQuestionStatuses(newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());

        ResearchCategorySummaryViewModel viewModel = populator.populate(application, loggedInUser, userIsLeadApplicant);

        assertTrue(viewModel.isClosed());
        assertTrue(viewModel.isComplete());
        assertFalse(viewModel.isCanMarkAsComplete());
        assertTrue(viewModel.isAllReadOnly());
        assertTrue(viewModel.isSummary());
        assertEquals(viewModel.getResearchCategory(), researchCategory.getName());
    }
}
