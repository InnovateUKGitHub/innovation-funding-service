package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;

/**
 * This class validates the FormInputResponse, it checks that it is a positive integer that is less than or equal to the
 * maximum score.
 */
public class AssessorScoreValidator extends BaseValidator {

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;

        if (ASSESSOR_SCORE == response.getFormInput().getType()) {
            int maxScore = response.getFormInput().getQuestion().getAssessorMaximumScore();
            String value = response.getValue();

            try {
                int assessorScore = Integer.parseInt(value);

                if (assessorScore < 0 || assessorScore > maxScore) {
                    rejectValue(errors, "value", "validation.assessor.score.betweenZeroAndMax", maxScore);
                }
            } catch (NumberFormatException e) {
                rejectValue(errors, "value","validation.assessor.score.notAnInteger");
            }
        }
    }
}
