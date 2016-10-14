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

    private String annexOne;
    private String annexTwo;
    private String annexThree;

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

    public String getAnnexOne() {
        return annexOne;
    }

    public void setAnnexOne(String annexOne) {
        this.annexOne = annexOne;
    }

    public String getAnnexTwo() {
        return annexTwo;
    }

    public void setAnnexTwo(String annexTwo) {
        this.annexTwo = annexTwo;
    }

    public String getAnnexThree() {
        return annexThree;
    }

    public void setAnnexThree(String annexThree) {
        this.annexThree = annexThree;
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
                .append(annexOne, that.annexOne)
                .append(annexTwo, that.annexTwo)
                .append(annexThree, that.annexThree)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(current)
                .append(text)
                .append(annexOne)
                .append(annexTwo)
                .append(annexThree)
                .toHashCode();
    }
}
