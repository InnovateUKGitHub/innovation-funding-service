package org.innovateuk.ifs.questionnaire.response.security;

import org.innovateuk.ifs.application.security.ApplicationSecurityHelper;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.questionnaire.link.repository.ApplicationOrganisationQuestionnaireResponseRepository;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkHasAnyProcessRole;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;

@Component
public class QuestionnaireResponseSecurityHelper {

    @Autowired
    private ApplicationOrganisationQuestionnaireResponseRepository applicationOrganisationQuestionnaireResponseRepository;

    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Autowired
    private ApplicationSecurityHelper applicationSecurityHelper;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    public boolean hasUpdateOrDeletePermission(UUID questionnaireResponseId, UserResource user) {
        QuestionnaireResponse questionnaireResponse = questionnaireResponseRepository.findById(questionnaireResponseId).orElseThrow(ObjectNotFoundException::new);
        switch (questionnaireResponse.getQuestionnaire().getSecurityType()) {
            case PUBLIC:
                return true;
            case USER_ONLY:
                return questionnaireResponse.getCreatedBy().getId().equals(user.getId());
            case LINK:
                return checkLinkUpdate(questionnaireResponse, user);
        }
        return false;
    }

    public boolean hasReadPermission(UUID questionnaireResponseId, UserResource user) {
        if (user.hasRole(Role.IFS_ADMINISTRATOR)) {
            return true;
        }
        QuestionnaireResponse questionnaireResponse = questionnaireResponseRepository.findById(questionnaireResponseId).orElseThrow(ObjectNotFoundException::new);
        switch (questionnaireResponse.getQuestionnaire().getSecurityType()) {
            case PUBLIC:
                return true;
            case USER_ONLY:
                return questionnaireResponse.getCreatedBy().getId().equals(user.getId());
            case LINK:
                return checkLinkRead(questionnaireResponse, user);
        }
        return false;
    }


    private boolean checkLinkRead(QuestionnaireResponse questionnaireResponse, UserResource user) {
        return applicationOrganisationQuestionnaireResponseRepository
                .findByQuestionnaireResponseId(questionnaireResponse.getId())
                .map(r -> applicationSecurityHelper.canViewApplication(r.getApplication().getId(), user))
                .orElse(false);
    }

    private boolean checkLinkUpdate(QuestionnaireResponse questionnaireResponse, UserResource user) {
        return applicationOrganisationQuestionnaireResponseRepository
                .findByQuestionnaireResponseId(questionnaireResponse.getId())
                .map(r -> checkHasAnyProcessRole(user, r.getApplication().getId(), r.getOrganisation().getId(), processRoleRepository, LEADAPPLICANT, COLLABORATOR))
                .orElse(false);
    }
}
