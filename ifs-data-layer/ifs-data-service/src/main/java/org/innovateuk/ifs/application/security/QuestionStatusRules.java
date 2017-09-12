package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.mapper.QuestionStatusMapper;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInnovationLead;

@Component
@PermissionRules
public class QuestionStatusRules {

    private static final Log LOG = LogFactory.getLog(QuestionStatusRules.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private QuestionStatusMapper questionStatusMapper;

    @PermissionRule(value = "READ", description = "Users can only read statuses of applications they are connected to")
    public boolean userCanReadQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        return userIsConnected(questionStatusResource.getApplication(), user);
    }

    @PermissionRule(value = "READ", description = "Support users can read statuses of all questions")
    public boolean supportCanReadQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        return isSupport(user);
    }

    @PermissionRule(value = "READ", description = "Innovation lead users can read statuses of all questions")
    public boolean innovationLeadCanReadQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        return isInnovationLead(user);
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

    private boolean userIsAllowed(final QuestionApplicationCompositeId ids, final UserResource user) {
        return questionHasMultipleStatuses(ids.questionId) || userIsAssigned(ids.questionId, ids.applicationId, user);
    }

    private boolean questionHasMultipleStatuses(final Long questionId) {
        return questionRepository.findOne(questionId).hasMultipleStatuses();
    }

    private boolean userIsConnected(Long applicationId, UserResource user){
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), applicationId);
        return processRole != null;
    }

    private boolean userIsAssigned(Long questionId, Long applicationId, UserResource user){
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), applicationId);
        return questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(
                questionId,
                applicationId,
                processRole.getId()
            ) != null;
    }

    private boolean userIsLeadApplicant(Long applicationId, UserResource user){
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), applicationId);
        return processRole.getRole().getName().equals(UserRoleType.LEADAPPLICANT.getName());
    }
}
