package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Generic form class to pass and save section data.
 */
public abstract class CompetitionSetupForm implements BindingResultTarget {

    private boolean markAsCompleteAction = true;

    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public void setMarkAsCompleteAction(boolean markAsCompleteAction) {
        this.markAsCompleteAction = markAsCompleteAction;
    }

    public boolean isMarkAsCompleteAction() {
        return markAsCompleteAction;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
    }
}
