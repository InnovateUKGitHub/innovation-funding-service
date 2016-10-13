package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An affiliation of a {@link User}. It may be used to describe personal and family appointments held, personal affiliations, other financial interests, and family financial interests depending on the {@link AffiliationType}.
 */
public class AffiliationResource {

    private Long id;
    private Long user;
    private AffiliationType affiliationType;
    private Boolean exists;
    private String relation;
    private String organisation;
    private String position;
    private String description;

    public AffiliationResource() {
    }

    AffiliationResource(Long user, AffiliationType affiliationType, Boolean exists, String relation, String organisation, String position, String description) {
        this.user = user;
        this.affiliationType = affiliationType;
        this.exists = exists;
        this.relation = relation;
        this.organisation = organisation;
        this.position = position;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public AffiliationType getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(AffiliationType affiliationType) {
        this.affiliationType = affiliationType;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AffiliationResource that = (AffiliationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(user, that.user)
                .append(affiliationType, that.affiliationType)
                .append(exists, that.exists)
                .append(relation, that.relation)
                .append(organisation, that.organisation)
                .append(position, that.position)
                .append(description, that.description)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(user)
                .append(affiliationType)
                .append(exists)
                .append(relation)
                .append(organisation)
                .append(position)
                .append(description)
                .toHashCode();
    }
}