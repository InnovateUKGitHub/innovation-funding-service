package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.viewmodel.InterviewFeedbackViewModel;
import org.innovateuk.ifs.interview.service.InterviewResponseRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewFeedbackViewModelPopulatorTest {

    @InjectMocks
    private InterviewFeedbackViewModelPopulator viewModelPopulator;

    @Mock
    private InterviewResponseRestService interviewResponseRestService;

    @Test
    public void testPopulate() {
        long applicationId = 1L;
        ProcessRoleResource role = newProcessRoleResource().withRole(Role.LEADAPPLICANT).build();
        when(interviewResponseRestService.findResponse(applicationId)).thenReturn(restSuccess(newFileEntryResource().withName("Filename").build()));

        InterviewFeedbackViewModel viewModel = viewModelPopulator.populate(applicationId, role);

        assertThat(viewModel.getResponseFilename(), is(equalTo("Filename")));
        assertThat(viewModel.isLeadApplicant(), is(true));
        assertThat(viewModel.hasAttachment(), is(true));
    }

}
