package org.innovateuk.ifs.application.creation.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import javax.validation.constraints.NotNull;

/**
 * Bean serves as a container for form parameters.
 */

public class ApplicationCreationAuthenticatedForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.field.confirm.new.application}")
    private Boolean createNewApplication;

    public Boolean getCreateNewApplication() {
        return createNewApplication;
    }

    public void setCreateNewApplication(Boolean createNewApplication) {
        this.createNewApplication = createNewApplication;
    }
}
