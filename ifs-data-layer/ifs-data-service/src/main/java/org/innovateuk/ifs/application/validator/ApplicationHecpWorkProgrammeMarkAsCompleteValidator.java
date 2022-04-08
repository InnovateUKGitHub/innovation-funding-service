package org.innovateuk.ifs.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * Validates the inputs in the application hecp work programme, if valid on the markAsComplete action
 */
@Slf4j
@Component
public class ApplicationHecpWorkProgrammeMarkAsCompleteValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        //Check subclasses for in case we receive hibernate proxy class.
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        log.debug("do ApplicationHecpWorkProgrammeMarkAsComplete Validation");

        ApplicationHorizonWorkProgramme applicationHorizonWorkProgramme = (ApplicationHorizonWorkProgramme) target;

        if (applicationHorizonWorkProgramme.getWorkProgramme() == null) {
            log.debug("MarkAsComplete application validation message for work programme is null");
            rejectValue(errors, "workProgramme", "validation.standard.horizon.work.programme.required");
        }

    }

}
