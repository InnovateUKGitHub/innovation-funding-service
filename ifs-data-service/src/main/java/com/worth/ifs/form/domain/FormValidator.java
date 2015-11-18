package com.worth.ifs.form.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * FormInputType is used to identify what response a FormInput needs.
 * This is also used to choose a template in the web-service. Depending on the FormInputType we
 * can also implement extra behaviour like form / input validation.
 */
@Entity
public class FormValidator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String clazzName;


    public FormValidator(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public FormValidator() {

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }
}
