package com.worth.ifs.user.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Contract Data Transfer Object
 */
public class ContractResource {

    private Long id;
    private boolean current;
    private String text;

    private String annexA;
    private String annexB;
    private String annexC;

    public ContractResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.text = text;
    }

    public String getAnnexA() {
        return annexA;
    }

    public void setAnnexA(String annexA) {
        this.annexA = annexA;
    }

    public String getAnnexB() {
        return annexB;
    }

    public void setAnnexB(String annexB) {
        this.annexB = annexB;
    }

    public String getAnnexC() {
        return annexC;
    }

    public void setAnnexC(String annexC) {
        this.annexC = annexC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ContractResource that = (ContractResource) o;

        return new EqualsBuilder()
                .append(current, that.current)
                .append(id, that.id)
                .append(text, that.text)
                .append(annexA, that.annexA)
                .append(annexB, that.annexB)
                .append(annexC, that.annexC)
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
