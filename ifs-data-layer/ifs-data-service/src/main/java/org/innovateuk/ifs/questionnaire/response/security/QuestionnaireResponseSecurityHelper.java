package org.innovateuk.ifs.questionnaire.response.security;

import org.innovateuk.ifs.application.security.ApplicationSecurityHelper;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.questionnaire.link.domain.ApplicationOrganisationQuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.link.domain.ProjectOrganisationQuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.link.repository.ApplicationOrganisationQuestionnaireResponseRepository;
import org.innovateuk.ifs.questionnaire.link.repository.ProjectOrganisationQuestionnaireResponseRepository;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.security.SecurityRuleUtil.checkHasAnyProcessRole;
import static org.innovateuk.ifs.user.resource.Authority.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;

@Component
public class QuestionnaireResponseSecurityHelper extends BasePermissionRules {

    @Autowired
    private ApplicationOrganisationQuestionnaireResponseRepository applicationOrganisationQuestionnaireResponseRepository;

    @Autowired
    private ProjectOrganisationQuestionnaireResponseRepository projectOrganisationQuestionnaireResponseRepository;

    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Autowired
    private ApplicationSecurityHelper applicationSecurityHelper;

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
        if (user.hasAuthority(IFS_ADMINISTRATOR)) {
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

        Optional<ApplicationOrganisationQuestionnaireResponse> maybeApplicationResponse = applicationOrganisationQuestionnaireResponseRepository
                .findByQuestionnaireResponseId(questionnaireResponse.getId());
        if (maybeApplicationResponse.isPresent()){
            return maybeApplicationResponse.map(r -> applicationSecurityHelper.canViewApplication(r.getApplication().getId(), user)).orElse(false);
        }

        Optional<ProjectOrganisationQuestionnaireResponse> maybeProjectResponse = projectOrganisationQuestionnaireResponseRepository
                .findByQuestionnaireResponseId(questionnaireResponse.getId());
        if (maybeProjectResponse.isPresent()){
            return maybeProjectResponse.map(r -> checkHasAnyProjectParticipantRole(user, r.getProject().getId(), ProjectParticipantRole.values())).orElse(false);
        }
        return false;
    }

    private boolean checkLinkUpdate(QuestionnaireResponse questionnaireResponse, UserResource user) {
        Optional<ApplicationOrganisationQuestionnaireResponse> maybeApplicationResponse = applicationOrganisationQuestionnaireResponseRepository
                .findByQuestionnaireResponseId(questionnaireResponse.getId());
        if (maybeApplicationResponse.isPresent()){
            return maybeApplicationResponse.map(r -> checkHasAnyProcessRole(user, r.getApplication().getId(), r.getOrganisation().getId(), processRoleRepository, LEADAPPLICANT, COLLABORATOR))
                    .orElse(false);
        }
        Optional<ProjectOrganisationQuestionnaireResponse> maybeProjectResponse = projectOrganisationQuestionnaireResponseRepository
                .findByQuestionnaireResponseId(questionnaireResponse.getId());
        if (maybeProjectResponse.isPresent()){
            return maybeProjectResponse.map(r -> checkHasAnyProjectParticipantRole(user, r.getProject().getId(), PROJECT_PARTNER))
                    .orElse(false);
        }
        return false;
    }



}
