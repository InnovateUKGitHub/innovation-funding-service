package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for an empty form input.
 */
public class EmptyInputViewModel extends AbstractFormInputViewModel {
    @Override
    protected FormInputType formInputType() {
        return FormInputType.EMPTY;
    }
}
