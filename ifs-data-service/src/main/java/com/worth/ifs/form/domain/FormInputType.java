package com.worth.ifs.form.domain;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;


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
    private String title;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name="form_type_form_validator",
            joinColumns={@JoinColumn(name="form_input_type_id")},
            inverseJoinColumns={@JoinColumn(name="form_validator_id")})
    private Set<FormValidator> formValidators;

    public FormInputType(Long id, String title) {
        this.id = id;
        this.title = title;
        formValidators = new LinkedHashSet<>();
    }

    public FormInputType() {

    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<FormValidator> getFormValidators() {
        return formValidators;
    }

    public void setFormValidators(Set<FormValidator> formValidators) {
        this.formValidators = formValidators;
    }

    public void addFormValidator(FormValidator formValidator) {
        this.formValidators.add(formValidator);
    }
}
