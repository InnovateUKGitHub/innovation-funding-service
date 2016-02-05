package com.worth.ifs.form.resource;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
