package org.innovateuk.ifs.assessment.review.controller.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessmentReviewApplicationSummaryModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private AssessmentReviewApplicationSummaryModelPopulator populator;

    @Mock
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Test
    public void populateModel() {
        CompetitionResource competition = newCompetitionResource().build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .build();
        AssessmentResource assessment = newAssessmentResource().build();
        UserResource user = newUserResource().build();
        ApplicationReadOnlyViewModel readOnlyViewModel = mock(ApplicationReadOnlyViewModel.class);

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessmentRestService.getByUserAndApplication(user.getId(), application.getId())).thenReturn(restSuccess(singletonList(assessment)));
        when(applicationReadOnlyViewModelPopulator.populate(application.getId(), user,
                ApplicationReadOnlySettings.defaultSettings().setAssessmentId(assessment.getId())))
                .thenReturn(readOnlyViewModel);

        AssessmentReviewApplicationSummaryViewModel viewModel = populator.populateModel(user, application.getId());

        assertEquals((long) application.getId(), viewModel.getApplicationId());
        assertEquals(readOnlyViewModel, viewModel.getApplicationReadOnlyViewModel());
        assertEquals(assessment, viewModel.getAssessment());
    }
}
