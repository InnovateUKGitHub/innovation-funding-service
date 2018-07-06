package org.innovateuk.ifs.application.forms.validator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.util.List;

/**
 * Abstract validator that can be used to check if a question can be edited or not.
 */
public abstract class QuestionEditableValidator {

    private QuestionService questionService;
    private QuestionSetupType questionType;

    protected QuestionEditableValidator(QuestionService questionService,
                                        QuestionSetupType questionType) {
        this.questionService = questionService;
        this.questionType = questionType;
    }

    public boolean questionAndApplicationHaveAllowedState(long questionId,
                                                             ApplicationResource applicationResource) {
        return questionIsAllowedType(questionId) &&
                applicationHasAllowedState(applicationResource) &&
                !questionIsMarkedAsComplete(questionId, applicationResource.getId());
    }

    protected boolean questionIsAllowedType(long questionId) {
        QuestionResource questionResource = questionService.getById(questionId);
        return questionResource.getQuestionSetupType().equals(questionType);
    }

    protected boolean applicationHasAllowedState(ApplicationResource applicationResource) {
        return applicationResource.isOpen();
    }

    protected boolean questionIsMarkedAsComplete(long questionId, long applicationId) {
        List<QuestionStatusResource> statusResourceList = questionService
                .findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
        return statusResourceList.stream().anyMatch(QuestionEditableValidator::markedAsComplete);
    }

    private static boolean markedAsComplete(QuestionStatusResource status) {
        return Boolean.TRUE.equals(status.getMarkedAsComplete());
    }
}
