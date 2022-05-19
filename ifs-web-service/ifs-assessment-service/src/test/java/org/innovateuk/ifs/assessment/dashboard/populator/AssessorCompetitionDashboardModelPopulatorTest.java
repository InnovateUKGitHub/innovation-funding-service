package org.innovateuk.ifs.assessment.dashboard.populator;

import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionDashboardViewModel;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.assessment.service.AssessorCompetitionDashboardRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionDashboardResourceBuilder.newAssessorCompetitionDashboardResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.PENDING;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorCompetitionDashboardModelPopulatorTest {

    @Mock
    private AssessorCompetitionDashboardRestService assessorCompetitionDashboardRestService;

    @InjectMocks
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Test
    public void populateModel() {
        long compId = 1L;
        long userId = 1L;

        List<ApplicationAssessmentResource> submittedAssessments = newApplicationAssessmentResource()
                .withApplicationId(1L, 2L)
                .withAssessmentId(1L, 2L)
                .withApplicationName("Application Name")
                .withLeadOrganisation("Organisation 1", "Organisation 2")
                .withState(SUBMITTED, SUBMITTED)
                .withOverallScore(50, 55)
                .withRecommended(TRUE, TRUE)
                .build(2);

        List<ApplicationAssessmentResource> outstandingAssessments = newApplicationAssessmentResource()
                .withApplicationId(3L, 4L)
                .withAssessmentId(3L, 4L)
                .withApplicationName("Application Name")
                .withLeadOrganisation("Organisation 3", "Organisation 4")
                .withState(PENDING, PENDING)
                .build(2);


        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = newAssessorCompetitionDashboardResource()
                .withCompetitionId(compId)
                .withCompetitionName("Competition Name")
                .withInnovationLead("Innovation Lead")
                .withOpenEndCompetition(false)
                .withBatchIndex((Long[]) null)
                .withAssessorAcceptDate(ZonedDateTime.now().minusDays(2))
                .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(4))
                .withApplicationAssessments(combineLists(submittedAssessments, outstandingAssessments))
                .build();

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        when(assessorCompetitionDashboardRestService.getAssessorCompetitionDashboard(compId, userId))
                .thenReturn(restSuccess(assessorCompetitionDashboardResource));

        when(publicContentItemRestService.getItemByCompetitionId(compId)).thenReturn(restSuccess(publicContentItemResource));

        AssessorCompetitionDashboardViewModel viewModel = assessorCompetitionDashboardModelPopulator.populateModel(compId, userId);
        assertEquals(viewModel.getCompetitionId(), assessorCompetitionDashboardResource.getCompetitionId());
        assertEquals(viewModel.getCompetitionTitle(), assessorCompetitionDashboardResource.getCompetitionName());
        assertEquals(viewModel.getLeadTechnologist(), assessorCompetitionDashboardResource.getInnovationLead());
        assertEquals(viewModel.isOpenEndCompetition(), assessorCompetitionDashboardResource.getOpenEndCompetition());
        assertEquals(viewModel.getBatchIndex(), assessorCompetitionDashboardResource.getBatchIndex());
        assertEquals(viewModel.getAcceptDeadline(), assessorCompetitionDashboardResource.getAssessorAcceptDate());
        assertEquals(viewModel.getSubmitDeadline(), assessorCompetitionDashboardResource.getAssessorDeadlineDate());
        assertEquals(viewModel.getSubmitted().size(), 2);
        assertEquals(viewModel.getOutstanding().size(), 2);

        verify(assessorCompetitionDashboardRestService, times(1)).getAssessorCompetitionDashboard(compId, userId);
        verifyNoMoreInteractions(assessorCompetitionDashboardRestService);
    }

    @Test
    public void populateModelWithAssessmentPeriod() {
        long compId = 1L;
        long userId = 1L;
        long assessmentPeriodId = 1L;

        List<ApplicationAssessmentResource> submittedAssessments = newApplicationAssessmentResource()
                .withApplicationId(1L, 2L)
                .withAssessmentId(1L, 2L)
                .withApplicationName("Application Name")
                .withLeadOrganisation("Organisation 1", "Organisation 2")
                .withState(SUBMITTED, SUBMITTED)
                .withOverallScore(50, 55)
                .withRecommended(TRUE, TRUE)
                .build(2);

        List<ApplicationAssessmentResource> outstandingAssessments = newApplicationAssessmentResource()
                .withApplicationId(3L, 4L)
                .withAssessmentId(3L, 4L)
                .withApplicationName("Application Name")
                .withLeadOrganisation("Organisation 3", "Organisation 4")
                .withState(PENDING, PENDING)
                .build(2);


        AssessorCompetitionDashboardResource assessorCompetitionDashboardResource = newAssessorCompetitionDashboardResource()
                .withCompetitionId(compId)
                .withCompetitionName("Competition Name")
                .withInnovationLead("Innovation Lead")
                .withOpenEndCompetition(true)
                .withBatchIndex(1L)
                .withAssessorAcceptDate(ZonedDateTime.now().minusDays(2))
                .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(4))
                .withApplicationAssessments(combineLists(submittedAssessments, outstandingAssessments))
                .build();

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        when(assessorCompetitionDashboardRestService.getAssessorCompetitionDashboard(compId, assessmentPeriodId, userId))
                .thenReturn(restSuccess(assessorCompetitionDashboardResource));

        when(publicContentItemRestService.getItemByCompetitionId(compId)).thenReturn(restSuccess(publicContentItemResource));

        AssessorCompetitionDashboardViewModel viewModel = assessorCompetitionDashboardModelPopulator.populateModel(compId, assessmentPeriodId, userId);
        assertEquals(viewModel.getCompetitionId(), assessorCompetitionDashboardResource.getCompetitionId());
        assertEquals(viewModel.getCompetitionTitle(), assessorCompetitionDashboardResource.getCompetitionName());
        assertEquals(viewModel.getLeadTechnologist(), assessorCompetitionDashboardResource.getInnovationLead());
        assertEquals(viewModel.isOpenEndCompetition(), assessorCompetitionDashboardResource.getOpenEndCompetition());
        assertEquals(viewModel.getBatchIndex(), assessorCompetitionDashboardResource.getBatchIndex());
        assertEquals(viewModel.getAcceptDeadline(), assessorCompetitionDashboardResource.getAssessorAcceptDate());
        assertEquals(viewModel.getSubmitDeadline(), assessorCompetitionDashboardResource.getAssessorDeadlineDate());
        assertEquals(viewModel.getSubmitted().size(), 2);
        assertEquals(viewModel.getOutstanding().size(), 2);

        verify(assessorCompetitionDashboardRestService, times(1)).getAssessorCompetitionDashboard(compId, assessmentPeriodId, userId);
        verifyNoMoreInteractions(assessorCompetitionDashboardRestService);
    }
}