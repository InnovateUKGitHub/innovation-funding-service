package org.innovateuk.ifs.form.domain;

import javax.persistence.*;


@Entity
public class MultipleChoiceOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="formInputId", referencedColumnName="id")
    private FormInput formInput;

    public MultipleChoiceOption(String text, FormInput formInput) {
        this.text = text;
        this.formInput = formInput;
        formInput.getMultipleChoiceOptions().add(this);
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FormInput getFormInput() {
        return formInput;
    }
}
