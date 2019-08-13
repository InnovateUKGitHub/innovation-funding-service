package org.innovateuk.ifs.assessment.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.review.populator.AssessmentReviewApplicationSummaryModelPopulator;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessmentReviewApplicationSummaryControllerTest extends BaseControllerMockMVCTest<AssessmentReviewApplicationSummaryController> {

    @Mock
    private AssessmentReviewApplicationSummaryModelPopulator assessmentReviewApplicationSummaryModelPopulator;

    @Override
    protected AssessmentReviewApplicationSummaryController supplyControllerUnderTest() {
        return new AssessmentReviewApplicationSummaryController();
    }

    @Test
    public void viewApplication() throws Exception {
        long reviewId = 1L;
        long applicationId = 2L;
        AssessmentReviewApplicationSummaryViewModel model = mock(AssessmentReviewApplicationSummaryViewModel.class);

        when(assessmentReviewApplicationSummaryModelPopulator.populateModel(getLoggedInUser(), applicationId)).thenReturn(model);

        mockMvc.perform(get("/review/{reviewId}/application/{applicationId}", reviewId, applicationId))
                .andExpect(view().name("assessor-panel-application-overview"))
                .andExpect(model().attribute("model", model));
    }
}
