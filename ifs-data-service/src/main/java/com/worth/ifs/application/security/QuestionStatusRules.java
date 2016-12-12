package com.worth.ifs.application.security;

import com.worth.ifs.application.mapper.QuestionStatusMapper;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import com.worth.ifs.application.resource.QuestionApplicationCompositeId;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.UserResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @PermissionRule(value = "READ", description = "users can only read statuses of applications thy are connected to")
    public boolean userCanReadQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        return userIsConnected(questionStatusResource.getApplication(), user);
    }

    @PermissionRule(value = "UPDATE", description = "users can only update statuses of questions they are assigned to")
    public boolean userCanUpdateQuestionStatus(QuestionStatusResource questionStatusResource, UserResource user){
        QuestionApplicationCompositeId ids = new QuestionApplicationCompositeId(questionStatusResource.getQuestion(), questionStatusResource.getApplication());
        return userCanUpdateQuestionStatusComposite(ids, user);
    }

    @PermissionRule(value = "UPDATE", description = "users can only update statuses of questions they are assigned to")
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
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(),  applicationId);
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
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(),  applicationId);
        return processRole.getRole().getName().equals(UserRoleType.LEADAPPLICANT.getName());
    }
}
