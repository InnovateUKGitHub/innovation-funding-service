package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionStatusResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.invite.InviteService;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Populator for the {@link AssignButtonsViewModel}
 */
@Component
public class AssignButtonsPopulator {

    private InviteService inviteService;

    public AssignButtonsPopulator(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    public AssignButtonsViewModel populate(AbstractApplicantResource resource, ApplicantQuestionResource question, boolean hideAssignButtons) {
        AssignButtonsViewModel viewModel = new AssignButtonsViewModel();
        viewModel.setAssignedBy(question.allAssignedStatuses()
                .map(ApplicantQuestionStatusResource::getAssignedBy).map(ApplicantResource::getProcessRole).findAny().orElse(null));
        viewModel.setAssignee(question.allAssignedStatuses()
                .map(ApplicantQuestionStatusResource::getAssignee).map(ApplicantResource::getProcessRole).findAny().orElse(null));
        viewModel.setLeadApplicant(resource.getApplicants().stream().filter(ApplicantResource::isLead).map(ApplicantResource::getProcessRole).findAny().orElse(null));
        viewModel.setCurrentApplicant(resource.getCurrentApplicant().getProcessRole());
        viewModel.setAssignableApplicants(resource.getApplicants().stream().map(ApplicantResource::getProcessRole).collect(Collectors.toList()));
        viewModel.setPendingAssignableUsers(inviteService.getPendingInvitationsByApplicationId(resource.getApplication().getId()));
        viewModel.setHideAssignButtons(hideAssignButtons);
        viewModel.setQuestion(question.getQuestion());
        return viewModel;
    }

}
