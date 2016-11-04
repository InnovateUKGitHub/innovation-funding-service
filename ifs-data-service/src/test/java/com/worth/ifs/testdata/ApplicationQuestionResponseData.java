package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.ApplicationResource;

/**
 * TODO DW - document this class
 */
public class ApplicationQuestionResponseData {

    private String questionName;
    private ApplicationResource application;

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public ApplicationResource getApplication() {
        return application;
    }
}
