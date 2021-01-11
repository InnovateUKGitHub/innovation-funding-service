package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationResearchParticipationViewModelPopulator;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationProcurementMilestoneViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationResearchParticipationViewModel;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.FinanceReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FinanceReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private FinanceReadOnlyViewModelPopulator populator;

    @Mock
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    @Mock
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Mock
    private ApplicationResearchParticipationViewModelPopulator applicationResearchParticipationViewModelPopulator;

    @Mock
    private ApplicationProcurementMilestoneViewModelPopulator applicationProcurementMilestoneViewModelPopulator;

    @Mock
    private SectionRestService sectionRestService;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
    }

    @Test
    public void populate() {
        setField(populator, "asyncFuturesGenerator", futuresGeneratorMock);
        ApplicationResource application = newApplicationResource()
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        SectionResource financeSection = newSectionResource().build();
        UserResource user = newUserResource().build();
        ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel = mock(ApplicationFinanceSummaryViewModel.class);
        ApplicationResearchParticipationViewModel applicationResearchParticipationViewModel = mock(ApplicationResearchParticipationViewModel.class);
        ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel = mock(ApplicationFundingBreakdownViewModel.class);
        ApplicationProcurementMilestoneViewModel applicationProcurementMilestoneViewModel = mock(ApplicationProcurementMilestoneViewModel.class);


        when(sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.FINANCE)).thenReturn(restSuccess(singletonList(financeSection)));
        when(applicationFinanceSummaryViewModelPopulator.populate(application.getId(), user)).thenReturn(applicationFinanceSummaryViewModel);
        when(applicationResearchParticipationViewModelPopulator.populate(application.getId())).thenReturn(applicationResearchParticipationViewModel);
        when(applicationFundingBreakdownViewModelPopulator.populate(application.getId(), user)).thenReturn(applicationFundingBreakdownViewModel);
        when(applicationProcurementMilestoneViewModelPopulator.populate(application)).thenReturn(applicationProcurementMilestoneViewModel);


        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, user, emptyList(), emptyList(),
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList());

        FinanceReadOnlyViewModel viewModel = populator.populate(data);

        assertEquals((long) financeSection.getId(), viewModel.getFinanceSectionId());
        assertEquals(applicationFinanceSummaryViewModel, viewModel.getApplicationFinanceSummaryViewModel());
        assertEquals(applicationFundingBreakdownViewModel, viewModel.getApplicationFundingBreakdownViewModel());
        assertEquals(applicationResearchParticipationViewModel, viewModel.getApplicationResearchParticipationViewModel());

        assertEquals("Finances summary", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
    }
}
