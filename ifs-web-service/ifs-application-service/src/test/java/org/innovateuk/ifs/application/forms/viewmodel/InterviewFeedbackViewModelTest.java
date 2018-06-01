package org.innovateuk.ifs.application.forms.viewmodel;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InterviewFeedbackViewModelTest {

    @Test
    public void testConstruct() {
        InterviewFeedbackViewModel leadWithResponse = new InterviewFeedbackViewModel("response", "feedback", true, false);

        assertThat(leadWithResponse.getBannerText(), is(equalTo(InterviewFeedbackViewModel.LEAD_WITH_RESPONSE_BANNER)));
        assertThat(leadWithResponse.hasResponse(), is(true));
        assertThat(leadWithResponse.hasFeedback(), is(true));

        InterviewFeedbackViewModel leadWithoutResponse = new InterviewFeedbackViewModel(null, "feedback", true, false);

        assertThat(leadWithoutResponse.getBannerText(), is(equalTo(InterviewFeedbackViewModel.LEAD_WITHOUT_RESPONSE_BANNER)));
        assertThat(leadWithoutResponse.hasResponse(), is(false));

        InterviewFeedbackViewModel collabWithResponse = new InterviewFeedbackViewModel("response", "feedback", false, false);

        assertThat(collabWithResponse.getBannerText(), is(equalTo(InterviewFeedbackViewModel.COLLAB_WITH_RESPONSE_BANNER)));

        InterviewFeedbackViewModel collabWithoutResponse = new InterviewFeedbackViewModel(null, "feedback", false, false);

        assertThat(collabWithoutResponse.getBannerText(), is(equalTo(InterviewFeedbackViewModel.COLLAB_WITHOUT_RESPONSE_BANNER)));

        InterviewFeedbackViewModel assessorWithResponse = new InterviewFeedbackViewModel("response", "feedback", false, true);

        assertThat(assessorWithResponse.getBannerText(), is(equalTo(InterviewFeedbackViewModel.ASSESSOR_WITH_RESPONSE_BANNER)));
    }
}
