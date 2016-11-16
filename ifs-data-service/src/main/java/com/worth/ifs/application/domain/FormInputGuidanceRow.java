package com.worth.ifs.application.domain;

import com.worth.ifs.form.domain.FormInput;

import javax.persistence.*;

/**
 * FormInputGuidanceRow defines database relations and a model to use client side and server side.
 */
@Entity
public class FormInputGuidanceRow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_input_id", referencedColumnName = "id")
    private FormInput forminput;

    private String subject;

    private String justification;
}
