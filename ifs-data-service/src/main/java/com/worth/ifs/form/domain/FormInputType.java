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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy="formInputType")
    private List<FormInput> formInputs;

    private String title;


    public FormInputType(Long id, String title, List<FormInput> formInputs) {
        this.id = id;
        this.title = title;
        this.formInputs = formInputs;
    }

    public FormInputType() {

    }

    public Long getId() {
        return id;
    }


    @JsonIgnore
    public List<FormInput> getFormInputs() {
        return formInputs;
    }

    public String getTitle() {
        return title;
    }
}
