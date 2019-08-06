package org.innovateuk.ifs.application;

import org.innovateuk.ifs.question.resource.QuestionSetupType;

import java.util.Optional;

import static java.lang.String.format;

public class ApplicationUrlHelper {

    //TODO IFS-5889 missing types RESEARCH_CATEGORY.
    public static Optional<String> getQuestionUrl(QuestionSetupType questionType, long questionId, long applicationId) {
        if (questionType != null) {
            switch (questionType) {
                case APPLICATION_DETAILS:
                    return Optional.of(format("/application/%d/form/question/%d/application-details", applicationId, questionId));
                case GRANT_AGREEMENT:
                    return Optional.of(format("/application/%d/form/question/%d/grant-agreement", applicationId, questionId));
                case GRANT_TRANSFER_DETAILS:
                    return Optional.of(format("/application/%d/form/question/%d/grant-transfer-details", applicationId, questionId));
                case APPLICATION_TEAM:
                    return Optional.of(format("/application/%d/form/question/%d/team", applicationId, questionId));
                case TERMS_AND_CONDITIONS:
                    return Optional.of(format("/application/%d/form/question/%d/terms-and-conditions", applicationId, questionId));
            }
            if (questionType.hasFormInputResponses()) {
                return Optional.of(format("/application/%d/form/question/%d/generic", applicationId, questionId));
            }
        }
        return Optional.empty();
    }
}
