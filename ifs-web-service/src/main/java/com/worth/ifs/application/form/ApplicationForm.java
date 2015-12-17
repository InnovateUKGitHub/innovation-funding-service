package com.worth.ifs.application.form;

import com.worth.ifs.application.resource.ApplicationResource;

import javax.validation.Valid;

/**
 * This class is used to setup and submit the form input values. On submit the values are converted into an Form object.
 * http://stackoverflow.com/a/4511716
 */
public class ApplicationForm extends Form {

    @Valid
    public ApplicationResource application;

    public ApplicationForm() {
        super();
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }
}
