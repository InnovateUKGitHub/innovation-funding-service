package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.feedback.populator.InterviewFeedbackViewModelPopulator;
import org.innovateuk.ifs.application.feedback.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewFeedbackViewModelPopulatorTest {

    @InjectMocks
    private InterviewFeedbackViewModelPopulator viewModelPopulator;

    @Mock
    private InterviewResponseRestService interviewResponseRestService;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Mock
    private UserRestService userRestService;

    @Test
    public void testPopulate() {
        long applicationId = 1L;
        UserResource user = newUserResource().withRolesGlobal(asList(Role.APPLICANT)).build();
        ProcessRoleResource role = newProcessRoleResource().withRole(Role.LEADAPPLICANT).build();
        when(interviewResponseRestService.findResponse(applicationId)).thenReturn(restSuccess(newFileEntryResource().withName("response").build()));
        when(interviewAssignmentRestService.findFeedback(applicationId)).thenReturn(restSuccess(newFileEntryResource().withName("feedback").build()));
        when(userRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(role));

        InterviewFeedbackViewModel viewModel = viewModelPopulator.populate(applicationId, user, true);

        assertThat(viewModel.getResponseFilename(), is(equalTo("response")));
        assertThat(viewModel.getFeedbackFilename(), is(equalTo("feedback")));
        assertThat(viewModel.isLeadApplicant(), is(true));
        assertThat(viewModel.hasResponse(), is(true));
        assertThat(viewModel.hasFeedback(), is(true));
        assertThat(viewModel.isFeedbackReleased(), is(true));
        assertThat(viewModel.isResponseSectionEnabled(), is(true));
        assertThat(viewModel.isAssessorOrAdmin(), is(false));
    }
}
