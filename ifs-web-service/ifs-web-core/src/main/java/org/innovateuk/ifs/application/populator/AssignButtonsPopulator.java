package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionStatusResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Populator for the {@link AssignButtonsViewModel}
 */
@Component
public class AssignButtonsPopulator {

    @Autowired
    private InviteRestService inviteRestService;

    public AssignButtonsViewModel populate(AbstractApplicantResource resource, ApplicantQuestionResource question, boolean hideAssignButtons) {
        AssignButtonsViewModel viewModel = new AssignButtonsViewModel();
        viewModel.setAssignedBy(question.allAssignedStatuses()
                .map(ApplicantQuestionStatusResource::getAssignedBy).findAny().orElse(null));
        viewModel.setAssignee(question.allAssignedStatuses()
                .map(ApplicantQuestionStatusResource::getAssignee).findAny().orElse(null));
        viewModel.setLeadApplicant(resource.getApplicants().stream().filter(ApplicantResource::isLead).findAny().orElse(null));
        viewModel.setCurrentApplicant(resource.getCurrentApplicant());
        viewModel.setAssignableApplicants(resource.getApplicants());
        viewModel.setPendingAssignableUsers(pendingInvitations(resource.getApplication()));
        viewModel.setHideAssignButtons(hideAssignButtons);
        viewModel.setQuestion(question.getQuestion());
        viewModel.setCurrentCompetitionIsOpen(resource.getCompetition().isOpen());
        return viewModel;
    }

    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(0),
                success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                        .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                        .collect(Collectors.toList()));
    }

}
