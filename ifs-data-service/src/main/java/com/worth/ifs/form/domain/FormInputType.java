package com.worth.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;


/**
 * FormInputType is used to identify what response a FormInput needs.
 * This is also used to choose a template in the web-service. Depending on the FormInputType we
 * can also implement extra behaviour like form / input validation.
 */
@Entity
public class FormInputType {
    @OneToMany(mappedBy="formInputType")
    private List<FormInput> formInput;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;


    public FormInputType(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public FormInputType() {
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

    @JsonIgnore
    public List<FormInput> getFormInput() {
        return formInput;
    }

    public void setFormInput(List<FormInput> formInput) {
        this.formInput = formInput;
    }
}
