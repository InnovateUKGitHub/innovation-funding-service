package com.worth.ifs.user.domain;

import com.worth.ifs.commons.util.AuditableEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Column(name = "annex_a")
    private String annexA;

    @Column(name = "annex_b")
    private String annexB;

    @Column(name = "annex_c")
    private String annexC;

    public Contract() {
        // default constructor
    }

    public Contract(String text, String annexA, String annexB, String annexC, boolean current) {
        setText(text);
        setAnnexA(annexA);
        setAnnexB(annexB);
        setAnnexC(annexC);
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

    public String getAnnexA() {
        return annexA;
    }

    public void setAnnexA(String annexA) {
        if (annexA == null) throw new NullPointerException("annexA cannot be null");
        if (annexA.isEmpty()) throw new IllegalArgumentException("annexA cannot be empty");
        this.annexA = annexA;
    }

    public String getAnnexB() {
        return annexB;
    }

    public void setAnnexB(String annexB) {
        if (annexB == null) throw new NullPointerException("annexB cannot be null");
        if (annexB.isEmpty()) throw new IllegalArgumentException("annexB cannot be empty");
        this.annexB = annexB;
    }

    public String getAnnexC() {
        return annexC;
    }

    public void setAnnexC(String annexC) {
        if (annexC == null) throw new NullPointerException("annexC cannot be null");
        if (annexC.isEmpty()) throw new IllegalArgumentException("annexC cannot be empty");
        this.annexC = annexC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Contract contract = (Contract) o;

        return new EqualsBuilder()
                .append(current, contract.current)
                .append(id, contract.id)
                .append(text, contract.text)
                .append(annexA, contract.annexA)
                .append(annexB, contract.annexB)
                .append(annexC, contract.annexC)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(current)
                .append(text)
                .append(annexA)
                .append(annexB)
                .append(annexC)
                .toHashCode();
    }
}
