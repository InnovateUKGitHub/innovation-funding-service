package org.innovateuk.ifs.application.forms.validator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component that can be used to check if application details can be edited or not.
 */
@Component
public class ApplicationDetailsEditableValidator {

    @Autowired
    private QuestionService questionService;

    public boolean questionAndApplicationHaveAllowedState(Long questionId, ApplicationResource applicationResource) {
        QuestionResource questionResource = questionService.getById(questionId);
        if (!questionResource.getShortName().equals(CompetitionSetupQuestionType.APPLICATION_DETAILS.getShortName())) {
            return false;
        }
        return applicationResource.isOpen() && !applicationDetailsIsMarkedAsComplete(questionId, applicationResource.getId());
    }

    private boolean applicationDetailsIsMarkedAsComplete(Long questionId, Long applicationId) {
        List<QuestionStatusResource> statusResourceList = questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
        return statusResourceList.stream().anyMatch(ApplicationDetailsEditableValidator::markedAsComplete);
    }

    private static boolean markedAsComplete(QuestionStatusResource status) {
        return Boolean.TRUE.equals(status.getMarkedAsComplete());
    }
}