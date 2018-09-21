package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewUserViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ApplicationOverviewUserModelPopulator {

    private final UserService userService;
    private UserRestService userRestService;

    public ApplicationOverviewUserModelPopulator(UserService userService,
                                                 UserRestService userRestService) {
        this.userService = userService;
        this.userRestService = userRestService;
    }

    public ApplicationOverviewUserViewModel populate(ApplicationResource application, long userId) {

        boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        boolean ableToSubmitApplication = isAbleToSubmitApplication(application, userIsLeadApplicant);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application.getId());

        final UserResource leadApplicant;
        if (leadApplicantProcessRole != null) {
            leadApplicant = userRestService.retrieveUserById(leadApplicantProcessRole.getUser()).getSuccess();
        } else {
            leadApplicant = null;
        }

        return new ApplicationOverviewUserViewModel(userIsLeadApplicant,
                                                    leadApplicant,
                                                    ableToSubmitApplication);
    }

    private boolean isAbleToSubmitApplication(ApplicationResource application, boolean userIsLeadApplicant) {
        return userIsLeadApplicant && application.isSubmittable();
    }

}
