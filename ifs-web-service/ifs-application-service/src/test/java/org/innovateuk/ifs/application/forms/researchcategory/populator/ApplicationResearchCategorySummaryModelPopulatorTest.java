package org.innovateuk.ifs.application.forms.researchcategory.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder;
import org.innovateuk.ifs.application.forms.researchcategory.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.junit.Assert.*;

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

        ResearchCategoryResource researchCategory = ResearchCategoryResourceBuilder.newResearchCategoryResource().withName("categoryName").build();
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withCompetition(competitionId)
                .withResearchCategory(researchCategory).build();
        QuestionResource question = QuestionResourceBuilder.newQuestionResource()
                .withCompetition(competitionId).build();
        ApplicantResource applicant = ApplicantResourceBuilder.newApplicantResource()
                .withOrganisation(OrganisationResourceBuilder.newOrganisationResource().build()).build();
        boolean userIsLeadApplicant = true;

        Mockito.when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY)).thenReturn(restSuccess(question));
            Mockito.when(applicantRestService.getQuestion(loggedInUser, application.getId(), question.getId()))
                .thenReturn(ApplicantQuestionResourceBuilder.newApplicantQuestionResource()
                        .withCurrentApplicant(applicant)
                        .withQuestion(question)
                        .withApplicantQuestionStatuses(ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource()
                                .withStatus(QuestionStatusResourceBuilder.newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());

        ResearchCategorySummaryViewModel viewModel = populator.populate(application, loggedInUser, userIsLeadApplicant);

        assertFalse(viewModel.isClosed());
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

        ResearchCategoryResource researchCategory = ResearchCategoryResourceBuilder.newResearchCategoryResource().withName("categoryName").build();
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withCompetition(competitionId)
                .withResearchCategory(researchCategory).build();
        QuestionResource question = QuestionResourceBuilder.newQuestionResource()
                .withCompetition(competitionId).build();
        OrganisationResource organisation = OrganisationResourceBuilder.newOrganisationResource().build();
        ApplicantResource collaborator = ApplicantResourceBuilder.newApplicantResource()
                .withOrganisation(organisation).build();
        boolean userIsLeadApplicant = false;

        Mockito.when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY)).thenReturn(restSuccess(question));
        Mockito.when(applicantRestService.getQuestion(loggedInUser, application.getId(), question.getId()))
                .thenReturn(ApplicantQuestionResourceBuilder.newApplicantQuestionResource()
                        .withCurrentApplicant(collaborator)
                        .withQuestion(question)
                        .withApplicantQuestionStatuses(ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource()
                                .withStatus(QuestionStatusResourceBuilder.newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());

        ResearchCategorySummaryViewModel viewModel = populator.populate(application, loggedInUser, userIsLeadApplicant);

        assertFalse(viewModel.isClosed());
        assertTrue(viewModel.isComplete());
        assertFalse(viewModel.isCanMarkAsComplete());
        assertTrue(viewModel.isAllReadOnly());
        assertTrue(viewModel.isSummary());
        assertEquals(viewModel.getResearchCategory(), researchCategory.getName());
    }
}
