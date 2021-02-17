package org.innovateuk.ifs.questionnaire.response.security;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.questionnaire.link.repository.ApplicationOrganisationQuestionnaireResponseRepository;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class QuestionnaireResponseSecurityHelper {

    @Autowired
    private ApplicationOrganisationQuestionnaireResponseRepository applicationOrganisationQuestionnaireResponseRepository;

    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    public boolean hasPermission(UUID questionnaireResponseId, UserResource user) {
        QuestionnaireResponse questionnaireResponse = questionnaireResponseRepository.findById(questionnaireResponseId).orElseThrow(ObjectNotFoundException::new);
        switch (questionnaireResponse.getQuestionnaire().getSecurityType()) {
            case PUBLIC:
                return true;
            case USER_ONLY:
                return questionnaireResponse.getCreatedBy().getId().equals(user.getId());
            case LINK:
                return checkLink(questionnaireResponse, user);
        }
        return false;
    }

    private boolean checkLink(QuestionnaireResponse questionnaireResponse, UserResource user) {
        return applicationOrganisationQuestionnaireResponseRepository
                .userCanEditQuestionnaireResponse(questionnaireResponse.getId(), user.getId());
    }
}
