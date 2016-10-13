package com.worth.ifs.user.domain;

import com.worth.ifs.commons.util.AuditableEntity;

import javax.persistence.*;

/**
 * A contract with main text and three appendices. The current contact is signed by assessors on registration (as part of
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

    @Column(name = "annex_1")
    private String annexOne;

    @Column(name = "annex_2")
    private String annexTwo;

    @Column(name = "annex_3")
    private String annexThree;

    public Contract() {
        // default constructor
    }

    public Contract(String text, String annexOne, String annexTwo, String annexThree, boolean current) {
        setText(text);
        setAnnexOne(annexOne);
        setAnnexTwo(annexTwo);
        setAnnexThree(annexThree);
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

    public String getAnnexOne() {
        return annexOne;
    }

    public void setAnnexOne(String annexOne) {
        if (annexOne == null) throw new NullPointerException("annexOne cannot be null");
        if (annexOne.isEmpty()) throw new IllegalArgumentException("annexOne cannot be empty");
        this.annexOne = annexOne;
    }

    public String getAnnexTwo() {
        return annexTwo;
    }

    public void setAnnexTwo(String annexTwo) {
        if (annexTwo == null) throw new NullPointerException("annexTwo cannot be null");
        if (annexTwo.isEmpty()) throw new IllegalArgumentException("annexTwo cannot be empty");
        this.annexTwo = annexTwo;
    }

    public String getAnnexThree() {
        return annexThree;
    }

    public void setAnnexThree(String annexThree) {
        if (annexThree == null) throw new NullPointerException("annexThree cannot be null");
        if (annexThree.isEmpty()) throw new IllegalArgumentException("annexThree cannot be empty");
        this.annexThree = annexThree;
    }
}
