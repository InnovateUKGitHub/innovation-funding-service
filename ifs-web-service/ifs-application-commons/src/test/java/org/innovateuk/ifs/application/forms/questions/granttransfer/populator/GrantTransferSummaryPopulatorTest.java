package org.innovateuk.ifs.application.forms.questions.granttransfer.populator;

import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder;
import org.innovateuk.ifs.application.populator.granttransfer.GrantTransferSummaryPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantAgreementSummaryViewModel;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantTransferDetailsSummaryViewModel;
import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.GRANT_AGREEMENT;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.GRANT_TRANSFER_DETAILS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantTransferSummaryPopulatorTest {

    @InjectMocks
    private GrantTransferSummaryPopulator populator;

    @Mock
    private EuGrantTransferRestService grantTransferRestService;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Test
    public void populateDetails() {
        Long loggedInUser = 1L;
        Long competitionId = 2L;

        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withCompetition(competitionId).build();
        QuestionResource question = QuestionResourceBuilder.newQuestionResource()
                .withCompetition(competitionId).build();
        ApplicantResource applicant = ApplicantResourceBuilder.newApplicantResource()
                .withOrganisation(OrganisationResourceBuilder.newOrganisationResource().build()).build();
        boolean userIsLeadApplicant = true;

        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, GRANT_TRANSFER_DETAILS)).thenReturn(restSuccess(question));
        when(applicantRestService.getQuestion(loggedInUser, application.getId(), question.getId()))
                .thenReturn(ApplicantQuestionResourceBuilder.newApplicantQuestionResource()
                        .withCurrentApplicant(applicant)
                        .withQuestion(question)
                        .withApplicantQuestionStatuses(ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource()
                                .withStatus(QuestionStatusResourceBuilder.newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());

        EuActionTypeResource actionTypeResource = new EuActionTypeResource();
        when(grantTransferRestService.findDetailsByApplicationId(application.getId())).thenReturn(restSuccess(newEuGrantTransferResource()
            .withActionType(actionTypeResource)
            .withFundingContribution(BigDecimal.TEN)
            .withProjectName("project")
            .withProjectStartDate(LocalDate.now())
            .withProjectEndDate(LocalDate.now())
            .withParticipantId("123456789")
            .withGrantAgreementNumber("123456")
            .withProjectCoordinator(true)
            .build()));

        GrantTransferDetailsSummaryViewModel viewModel = populator.populateDetails(application, loggedInUser, userIsLeadApplicant);

        assertEquals(viewModel.getActionType(), actionTypeResource);
        assertEquals(viewModel.getFundingContribution(), BigDecimal.TEN);
        assertEquals(viewModel.getProjectName(), "project");
        assertEquals(viewModel.getParticipantId(), "123456789");
        assertEquals(viewModel.getGrantAgreementNumber(), "123456");
        assertEquals(viewModel.getProjectCoordinator(), true);

        assertTrue(viewModel.isClosed());
        assertTrue(viewModel.isComplete());
        assertTrue(viewModel.isCanMarkAsComplete());
        assertTrue(viewModel.isAllReadOnly());
        assertTrue(viewModel.isSummary());
    }

    @Test
    public void populateAgreement() {
        Long loggedInUser = 1L;
        Long competitionId = 2L;

        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withCompetition(competitionId).build();
        QuestionResource question = QuestionResourceBuilder.newQuestionResource()
                .withCompetition(competitionId).build();
        ApplicantResource applicant = ApplicantResourceBuilder.newApplicantResource()
                .withOrganisation(OrganisationResourceBuilder.newOrganisationResource().build()).build();
        boolean userIsLeadApplicant = true;

        when(grantTransferRestService.findGrantAgreement(application.getId())).thenReturn(restSuccess(newFileEntryResource().withName("filename").build()));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, GRANT_AGREEMENT)).thenReturn(restSuccess(question));
        when(applicantRestService.getQuestion(loggedInUser, application.getId(), question.getId()))
                .thenReturn(ApplicantQuestionResourceBuilder.newApplicantQuestionResource()
                        .withCurrentApplicant(applicant)
                        .withQuestion(question)
                        .withApplicantQuestionStatuses(ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource()
                                .withStatus(QuestionStatusResourceBuilder.newQuestionStatusResource().withMarkedAsComplete(true).build())
                                .build(1))
                        .build());

        GrantAgreementSummaryViewModel viewModel = populator.populateAgreement(application, loggedInUser, userIsLeadApplicant);

        assertEquals(viewModel.getFilename(), "filename");

        assertTrue(viewModel.isClosed());
        assertTrue(viewModel.isComplete());
        assertTrue(viewModel.isCanMarkAsComplete());
        assertTrue(viewModel.isAllReadOnly());
        assertTrue(viewModel.isSummary());
    }

}
