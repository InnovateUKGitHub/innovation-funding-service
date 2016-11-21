package com.worth.ifs.form.resource;

import java.util.List;

public class FormInputTypeResource {
    private Long id;
    private String title;
    private List<Long> formInput;


    public FormInputTypeResource(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public FormInputTypeResource() {
    	// no-arg constructor
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getFormInput() {
        return formInput;
    }

    public void setFormInput(List<Long> formInput) {
        this.formInput = formInput;
    }
}
