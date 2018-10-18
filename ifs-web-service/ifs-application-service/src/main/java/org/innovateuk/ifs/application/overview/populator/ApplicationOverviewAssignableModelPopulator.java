package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewAssignableViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

@Component
public class ApplicationOverviewAssignableModelPopulator {

    private QuestionService questionService;
    private ProcessRoleService processRoleService;
    private InviteService inviteService;

    public ApplicationOverviewAssignableModelPopulator(QuestionService questionService,
                                                       ProcessRoleService processRoleService,
                                                       InviteService inviteService) {
        this.questionService = questionService;
        this.processRoleService = processRoleService;
        this.inviteService = inviteService;
    }

    public ApplicationOverviewAssignableViewModel populate(ApplicationResource application, Optional<OrganisationResource> userOrganisation, long userId) {

        if (isApplicationInViewMode(application, userOrganisation)) {
            return new ApplicationOverviewAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees =
                questionService.getQuestionStatusesForApplicationAndOrganisation(application.getId(), userOrganisation.get().getId());

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);
        List<ApplicationInviteResource> pendingAssignableUsers = inviteService.getPendingInvitationsByApplicationId(application.getId());

        Future<List<ProcessRoleResource>> assignableUsers = processRoleService.findAssignableProcessRoles(application.getId());

        return new ApplicationOverviewAssignableViewModel(assignableUsers, pendingAssignableUsers, questionAssignees, notifications);
    }

    private boolean isApplicationInViewMode(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        return !application.isOpen() || !userOrganisation.isPresent();
    }
}
