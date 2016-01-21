package com.worth.ifs.form.resource;

import org.springframework.stereotype.Component;


public class FormInputTypeResource {
    private Long id;
    private String title;


    public FormInputTypeResource(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public FormInputTypeResource() {

    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
