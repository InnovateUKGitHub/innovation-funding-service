package org.innovateuk.ifs.application.feedback.viewmodel;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InterviewFeedbackViewModelTest {

    @Test
    public void testConstruct() {
        InterviewFeedbackViewModel leadWithResponse = new InterviewFeedbackViewModel(1l, "compName", "response", "feedback", true, true, false);

        assertThat(leadWithResponse.getResponseBannerText(), is(equalTo(InterviewFeedbackViewModel.LEAD_WITH_RESPONSE_BANNER)));
        assertThat(leadWithResponse.hasResponse(), is(true));
        assertThat(leadWithResponse.hasFeedback(), is(true));
        assertThat(leadWithResponse.isResponseSectionEnabled(), is(true));

        InterviewFeedbackViewModel leadWithoutResponse = new InterviewFeedbackViewModel(1l, "compName", null, "feedback", true, true, false);

        assertThat(leadWithoutResponse.getNoResponseBannerText(), is(equalTo(InterviewFeedbackViewModel.LEAD_WITHOUT_RESPONSE_BANNER)));
        assertThat(leadWithoutResponse.hasResponse(), is(false));
        assertThat(leadWithoutResponse.isResponseSectionEnabled(), is(false));

        InterviewFeedbackViewModel collabWithResponse = new InterviewFeedbackViewModel(1l, "compName", "response", "feedback", false, false, false);

        assertThat(collabWithResponse.getResponseBannerText(), is(equalTo(InterviewFeedbackViewModel.COLLAB_WITH_RESPONSE_BANNER)));

        InterviewFeedbackViewModel collabWithoutResponse = new InterviewFeedbackViewModel(1l, "compName", null, "feedback", false, false, false);

        assertThat(collabWithoutResponse.getNoResponseBannerText(), is(equalTo(InterviewFeedbackViewModel.COLLAB_WITHOUT_RESPONSE_BANNER)));

        InterviewFeedbackViewModel assessorWithResponse = new InterviewFeedbackViewModel(1l, "compName", "response", "feedback", false, true, true);

        assertThat(assessorWithResponse.getResponseBannerText(), is(equalTo(InterviewFeedbackViewModel.ASSESSOR_WITH_RESPONSE_BANNER)));
    }
}
