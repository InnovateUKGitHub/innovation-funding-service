package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewAssignableViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
public class ApplicationOverviewAssignableModelPopulator {

    private QuestionService questionService;
    private ProcessRoleService processRoleService;
    private InviteRestService inviteRestService;

    public ApplicationOverviewAssignableModelPopulator(QuestionService questionService, ProcessRoleService processRoleService, InviteRestService inviteRestService) {
        this.questionService = questionService;
        this.processRoleService = processRoleService;
        this.inviteRestService = inviteRestService;
    }

    public ApplicationOverviewAssignableViewModel populate(ApplicationResource application, Optional<OrganisationResource> userOrganisation, Long userId) {

        if (isApplicationInViewMode(application, userOrganisation)) {
            return new ApplicationOverviewAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees = questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.get().getId());

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);
        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        Future<List<ProcessRoleResource>> assignableUsers = processRoleService.findAssignableProcessRoles(application.getId());

        return new ApplicationOverviewAssignableViewModel(assignableUsers, pendingAssignableUsers, questionAssignees, notifications);
    }

    private boolean isApplicationInViewMode(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        return !application.isOpen() || !userOrganisation.isPresent();
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
