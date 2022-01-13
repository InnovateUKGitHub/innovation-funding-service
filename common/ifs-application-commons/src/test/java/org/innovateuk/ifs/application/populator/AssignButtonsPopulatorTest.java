package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder;
import org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AssignButtonsPopulator}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AssignButtonsPopulatorTest {

    @InjectMocks
    private AssignButtonsPopulator assignButtonsPopulator;

    @Mock
    private InviteService inviteService;

    private ApplicantResource assignedBy = applicantResource(false).build();
    private ApplicantResource assignee = applicantResource(true).build();
    private ApplicantResource completedBy = applicantResource(false).build();

    @Test
    public void testPopulate() {
        ApplicantQuestionResource question = applicantQuestionResource().build();
        boolean hideAssignButtons = false;
        List<ApplicationInviteResource> invites = newApplicationInviteResource().withStatus(InviteStatus.SENT).build(1);
        when(inviteService.getPendingInvitationsByApplicationId(question.getApplication().getId())).thenReturn(invites);
        AssignButtonsViewModel viewModel = assignButtonsPopulator.populate(question, question, hideAssignButtons);

        assertThat(viewModel.getAssignedBy(), equalTo(assignedBy.getProcessRole()));
        assertThat(viewModel.getAssignableApplicants(), equalTo(question.getApplicants().stream().map(ApplicantResource::getProcessRole).collect(toList())));
        assertThat(viewModel.getAssignee(), equalTo(assignee.getProcessRole()));
        assertThat(viewModel.getLeadApplicant(), equalTo(assignee.getProcessRole()));
        assertThat(viewModel.getCurrentApplicant(), equalTo(assignee.getProcessRole()));
        assertThat(viewModel.getQuestion(), equalTo(question.getQuestion()));
        assertThat(viewModel.getPendingAssignableUsers(), equalTo(invites));

        assertThat(viewModel.isAssigned(), equalTo(true));
        assertThat(viewModel.isAssignedByCurrentUser(), equalTo(false));
        assertThat(viewModel.isAssignedByLead(), equalTo(false));
        assertThat(viewModel.isAssignedTo(assignee.getProcessRole()), equalTo(true));
        assertThat(viewModel.isAssignedTo(assignedBy.getProcessRole()), equalTo(false));
        assertThat(viewModel.isAssignedToLead(), equalTo(true));
        assertThat(viewModel.isHideAssignButtons(), equalTo(hideAssignButtons));
        assertThat(viewModel.isNotAssigned(), equalTo(false));
    }

    private ApplicantQuestionResourceBuilder applicantQuestionResource() {
        return newApplicantQuestionResource()
                .withApplication(newApplicationResource().build())
                .withCompetition(newCompetitionResource().build())
                .withCurrentApplicant(assignee)
                .withCurrentUser(newUserResource().build())
                .withApplicants(asList(assignedBy, assignee, completedBy))

                .withQuestion(newQuestionResource().build())
                .withApplicantQuestionStatuses(newApplicantQuestionStatusResource()
                        .withStatus(newQuestionStatusResource().withMarkedAsComplete(false).build())
                        .withAssignedBy(assignedBy)
                        .withAssignee(assignee)
                        .withMarkedAsCompleteBy(completedBy)
                        .build(3));
    }

    private ApplicantResourceBuilder applicantResource(boolean lead) {
        return newApplicantResource()
                .withProcessRole(newProcessRoleResource()
                        .withRole(lead ? LEADAPPLICANT : COLLABORATOR)
                        .withUser(newUserResource().build())
                        .build())
                .withOrganisation(newOrganisationResource().build());
    }
}