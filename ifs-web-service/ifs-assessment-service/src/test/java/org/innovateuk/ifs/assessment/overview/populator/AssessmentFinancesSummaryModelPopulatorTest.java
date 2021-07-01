package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationProcurementMilestoneSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationProcurementMilestonesSummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentFinancesSummaryViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessmentFinancesSummaryModelPopulatorTest {

    @InjectMocks
    private AssessmentFinancesSummaryModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Mock
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private ApplicationProcurementMilestoneSummaryViewModelPopulator applicationProcurementMilestoneSummaryViewModelPopulator;

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.PROCUREMENT)
                .withProcurementMilestones(true)
                .withAssessorAcceptsDate(ZonedDateTime.now())
                .withAssessorDeadlineDate(ZonedDateTime.now())
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        AssessmentResource assessment = newAssessmentResource()
                .withApplication(application.getId())
                .withApplicationName("Application-1")
                .withCompetition(competition.getId())
                .build();
        UserResource user = newUserResource().build();

        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = mock(ApplicationFundingBreakdownViewModel.class);
        ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel = mock(ApplicationFinanceSummaryViewModel.class);
        ApplicationProcurementMilestonesSummaryViewModel applicationProcurementMilestonesSummaryViewModel = mock(ApplicationProcurementMilestonesSummaryViewModel.class);

        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(RestResult.restSuccess(competition));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(RestResult.restSuccess(application));
        when(applicationFundingBreakdownViewModelPopulator.populate(application.getId(), user)).thenReturn(applicationFundingBreakdownViewModel);
        when(applicationFinanceSummaryViewModelPopulator.populate(application.getId(), user)).thenReturn(applicationFinanceSummaryViewModel);
        when(applicationProcurementMilestoneSummaryViewModelPopulator.populate(application)).thenReturn(applicationProcurementMilestonesSummaryViewModel);

        AssessmentFinancesSummaryViewModel viewModel = populator.populateModel(assessment.getId(), user);

        assertEquals(assessment.getId().longValue(), viewModel.getAssessmentId());
        assertEquals(assessment.getApplication().longValue(), viewModel.getApplicationId());
        assertEquals(competition.getFundingType(), viewModel.getFundingType());
        assertEquals(assessment.getApplicationName(), viewModel.getApplicationName());
        assertEquals(applicationFinanceSummaryViewModel, viewModel.getApplicationFinanceSummaryViewModel());
        assertEquals(applicationFundingBreakdownViewModel, viewModel.getApplicationFundingBreakdownViewModel());
        assertEquals(applicationProcurementMilestonesSummaryViewModel, viewModel.getApplicationProcurementMilestonesSummaryViewModel());
        assertFalse(viewModel.isKtpCompetition());
        assertTrue(viewModel.isProcurementMilestones());
    }
}
