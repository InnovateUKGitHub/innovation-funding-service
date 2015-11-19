package com.worth.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.validation.Validator;

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


    public FormValidator(String title) {
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
}
