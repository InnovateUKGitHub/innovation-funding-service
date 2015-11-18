package com.worth.ifs.form.domain;

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
    private String title;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name="form_type_form_validator",
            joinColumns={@JoinColumn(name="form_input_type_id")},
            inverseJoinColumns={@JoinColumn(name="form_validator_id")})
    private List<FormValidator> formValidators;

    public FormInputType(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public FormInputType() {

    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<FormValidator> getFormValidators() {
        return formValidators;
    }

    public void setFormValidators(List<FormValidator> formValidators) {
        this.formValidators = formValidators;
    }
}
