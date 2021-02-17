package org.innovateuk.ifs.questionnaire.response.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@PermissionRules
@Component
public class QuestionnaireResponsePermissionRules extends BasePermissionRules {

    @Autowired
    private QuestionnaireResponseSecurityHelper securityHelper;

    @PermissionRule(value = "READ", description = "Read rule defined by questionnaire")
    public boolean readResponse(final QuestionnaireResponseResource questionnaireResponseResource, final UserResource user) {
        return securityHelper.hasPermission(UUID.fromString(questionnaireResponseResource.getId()), user);
    }

    @PermissionRule(value = "READ", description = "Admin can read questionnaire responses")
    public boolean readResponseAdmin(final QuestionnaireResponseResource questionnaireResponseResource, final UserResource user) {
        return user.hasRole(Role.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "UPDATE_CREATE_OR_DELETE", description = "Read rule defined by questionnaire")
    public boolean update(final QuestionnaireResponseResource questionnaireResponseResource, final UserResource user) {
        return securityHelper.hasPermission(UUID.fromString(questionnaireResponseResource.getId()), user);
    }

    @PermissionRule(value = "READ", description = "Read rule defined by questionnaire")
    public boolean readResponse(final QuestionnaireQuestionResponseResource questionnaireResponseResource, final UserResource user) {
        return securityHelper.hasPermission(UUID.fromString(questionnaireResponseResource.getQuestionnaireResponse()), user);
    }

    @PermissionRule(value = "READ", description = "Admin can read questionnaire responses")
    public boolean readResponseAdmin(final QuestionnaireQuestionResponseResource questionnaireResponseResource, final UserResource user) {
        return user.hasRole(Role.IFS_ADMINISTRATOR);
    }

    @PermissionRule(value = "UPDATE_CREATE_OR_DELETE", description = "Read rule defined by questionnaire")
    public boolean update(final QuestionnaireQuestionResponseResource questionnaireResponseResource, final UserResource user) {
        return securityHelper.hasPermission(UUID.fromString(questionnaireResponseResource.getQuestionnaireResponse()), user);
    }
}
