package com.worth.ifs.form.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.validation.Validator;


public class FormValidatorResource {
    private Long id;
    private String title;
    private String clazzName;

    public FormValidatorResource(String title) {
        this.title = title;
    }

    public FormValidatorResource() {
    	// no-arg constructor
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public String getClazzName() {
        return clazzName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    @JsonIgnore
    public void setClazz(Class clazz) {
        this.clazzName = clazz.getName();
    }

    @JsonIgnore
    public Class<Validator> getClazz() throws ClassNotFoundException {
        return (Class<Validator>) Class.forName(this.clazzName);
    }

    public void setId(Long id) {
        this.id = id;
    }
}
