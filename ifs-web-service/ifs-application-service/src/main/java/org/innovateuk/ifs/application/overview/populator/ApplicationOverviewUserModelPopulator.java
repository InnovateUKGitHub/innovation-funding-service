package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewUserViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationOverviewUserModelPopulator {

    private UserService userService;

    public ApplicationOverviewUserModelPopulator(UserService userService) {
        this.userService = userService;
    }

    public ApplicationOverviewUserViewModel populate(ApplicationResource application, Long userId) {

        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        Boolean ableToSubmitApplication = isAbleToSubmitApplication(application, userIsLeadApplicant);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application.getId());
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        return new ApplicationOverviewUserViewModel(userIsLeadApplicant,
                                                    leadApplicant,
                                                    ableToSubmitApplication);
    }

    private boolean isAbleToSubmitApplication(ApplicationResource application, boolean userIsLeadApplicant) {
        return userIsLeadApplicant && application.isSubmittable();
    }

}
