package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@Component
@PermissionRules
public class QuestionStatusRules extends BasePermissionRules {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @PermissionRule(value = "READ", description = "Users can only read statuses of applications they are connected to")
    public boolean userCanReadQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        return userIsConnected(questionStatusResource.getApplication(), user);
    }

    @PermissionRule(value = "READ", description = "Innovation lead users can read statuses of all questions")
    public boolean internalUserCanReadQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        return isInternal(user);
    }

    @PermissionRule(value = "UPDATE", description = "Users can only update statuses of questions they are assigned to")
    public boolean userCanUpdateQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionStatusResource.getQuestion(), questionStatusResource.getApplication());
        return userCanUpdateQuestionStatusComposite(ids, user);
    }

    @PermissionRule(value = "UPDATE", description = "Users can only update statuses of questions they are assigned to")
    public boolean userCanUpdateQuestionStatusComposite(QuestionApplicationCompositeId ids, UserResource user) {
        return userIsLeadApplicant(ids.applicationId, user) || (userIsAllowed(ids, user) && userIsConnected(ids.applicationId, user));
    }

    @PermissionRule(value = "MARK_TEAM_INCOMPLETE", description = "Any users that are connected can mark the application team as incomplete")
    public boolean userCanMarkApplicationTeamAsIncomplete(QuestionApplicationCompositeId ids, UserResource user) {
        return userIsConnected(ids.applicationId, user);
    }

    @PermissionRule(value = "MARK_SECTION", description = "Only member of project team can mark a section as complete")
    public boolean onlyMemberOfProjectTeamCanMarkSection(ApplicationResource applicationResource, UserResource user) {
        return isMemberOfProjectTeam(applicationResource.getId(), user);
    }

    private boolean userIsAllowed(final QuestionApplicationCompositeId ids, final UserResource user) {
        return questionHasMultipleStatuses(ids.questionId) || userIsAssigned(ids.questionId, ids.applicationId, user);
    }

    private boolean questionHasMultipleStatuses(final Long questionId) {
        return questionRepository.findOne(questionId).hasMultipleStatuses();
    }

    private boolean userIsConnected(Long applicationId, UserResource user){
        return processRoleRepository.existsByUserIdAndApplicationId(user.getId(), applicationId);
    }

    private boolean userIsAssigned(Long questionId, Long applicationId, UserResource user){
        ProcessRole processRole = processRoleRepository.findOneByUserIdAndRoleInAndApplicationId(user.getId(), applicantProcessRoles(), applicationId);
        return questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(
                questionId,
                applicationId,
                processRole.getId()
            ) != null;
    }

    private boolean userIsLeadApplicant(Long applicationId, UserResource user){
        return processRoleRepository.existsByUserIdAndApplicationIdAndRole(user.getId(), applicationId, Role.LEADAPPLICANT);
    }
}