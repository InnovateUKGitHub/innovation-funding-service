package com.worth.ifs.application.resource;

public class FormInputGuidanceRowResource {
    private Long id;
    private String subject;
    private String justification;
    private Long formInput;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Long getFormInput() {
        return formInput;
    }

    public void setFormInput(Long formInput) {
        this.formInput = formInput;
    }
}
