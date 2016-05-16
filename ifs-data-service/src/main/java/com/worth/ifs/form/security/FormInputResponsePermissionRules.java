package com.worth.ifs.form.security;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.security.ApplicationRules;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.user.resource.UserRoleType.*;

@PermissionRules
@Component
public class FormInputResponsePermissionRules {
    private static final Log LOG = LogFactory.getLog(ApplicationRules.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @PermissionRule(value = "READ", description = "The consortium can see the input responses of their organisation and application")
    public boolean consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(final FormInputResponseResource response, final UserResource user) {
        final boolean isLeadApplicantForOrganisation = checkRoleForApplicationAndOrganisation(user, response, LEADAPPLICANT);
        final boolean isCollaboratorForOrganisation = checkRoleForApplicationAndOrganisation(user, response, COLLABORATOR);
        return isLeadApplicantForOrganisation || isCollaboratorForOrganisation;
    }

    @PermissionRule(value = "READ", description = "The consortium can see the input responses of the application when the response is shared between organisations")
    public boolean consortiumCanSeeTheInputResponsesForApplicationWhenSharedBetweenOrganisations(final FormInputResponseResource response, final UserResource user) {
        final FormInput formInput = formInputRepository.findOne(response.getFormInput());
        final Question question = formInput.getQuestion();
        if (!question.getMultipleStatuses()){
            final boolean isLeadApplicant  = checkRole(user, response.getApplication(), LEADAPPLICANT, processRoleRepository);
            final boolean isCollaborator  = checkRole(user, response.getApplication(), COLLABORATOR, processRoleRepository);
            return isCollaborator || isLeadApplicant;
        }
        return false;
    }

    @PermissionRule(value = "READ", description = "The assessor can see the input responses of in applications for the applications they assess")
    public boolean assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyAssess(final FormInputResponseResource response, final UserResource user) {
        final boolean isAssessor = checkRoleForApplicationAndOrganisation(user, response, ASSESSOR);
        return isAssessor;
    }

    @PermissionRule(value = "READ", description = "A comp admin can see form input responses for applications")
    public boolean compAdminCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        return isCompAdmin(user);
    }

    private boolean checkRoleForApplicationAndOrganisation(UserResource user, FormInputResponseResource response, UserRoleType userRoleType) {
        final List<Role> roles = roleRepository.findByName(userRoleType.getName());
        final Role role = roles.get(0);
        final Long organisationId = processRoleRepository.findOne(response.getUpdatedBy()).getOrganisation().getId();
        final Long applicationId = response.getApplication();
        return checkRole(user, applicationId, organisationId, userRoleType, roleRepository, processRoleRepository);
    }

}
