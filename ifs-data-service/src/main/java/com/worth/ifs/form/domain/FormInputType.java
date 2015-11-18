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
}
