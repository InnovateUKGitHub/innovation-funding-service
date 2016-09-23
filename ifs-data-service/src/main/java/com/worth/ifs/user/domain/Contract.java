package com.worth.ifs.user.domain;

import com.worth.ifs.commons.util.AuditableEntity;

import javax.persistence.*;

/**
 * A contract with main text and two appendicies. The current contact is signed by assessors on registraion (as part of
 * their {@link Profile}) and again in subsequent years.
 * <p>
 * There must be only one <em>current</em> contract.
 *
 * @see Profile
 */
@Entity
public class Contract extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean current;

    private String text;

    @Column(name = "appendix_1")
    private String appendixOne;

    @Column(name = "appendix_2")
    private String appendixTwo;

    Contract() {
        // default constructor
    }

    public Contract(String text, String appendixOne, String appendixTwo, boolean current) {
        setText(text);
        setAppendixOne(appendixOne);
        setAppendixTwo(appendixTwo);
        this.current = current;
    }

    public Long getId() {
        return id;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) throw new NullPointerException("text cannot be null");
        if (text.isEmpty()) throw new IllegalArgumentException("text cannot be empty");
        this.text = text;
    }

    public String getAppendixOne() {
        return appendixOne;
    }

    public void setAppendixOne(String appendixOne) {
        if (appendixOne == null) throw new NullPointerException("appendixOne cannot be null");
        if (appendixOne.isEmpty()) throw new IllegalArgumentException("appendixOne cannot be empty");
        this.appendixOne = appendixOne;
    }

    public String getAppendixTwo() {
        return appendixTwo;
    }

    public void setAppendixTwo(String appendixTwo) {
        if (appendixTwo == null) throw new NullPointerException("appendixTwo cannot be null");
        if (appendixTwo.isEmpty()) throw new IllegalArgumentException("appendixTwo cannot be empty");
        this.appendixTwo = appendixTwo;
    }
}
