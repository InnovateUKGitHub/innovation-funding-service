package com.worth.ifs.user.resource;

public class AffiliationResourceBuilder {

    // XXX this is odd

    private Long user;
    private AffiliationType affiliationType;
    private Boolean exists;
    private String relation;
    private String organisation;
    private String position;
    private String description;

    public AffiliationResourceBuilder setUser(Long user) {
        this.user = user;
        return this;
    }

    public AffiliationResourceBuilder setAffiliationType(AffiliationType affiliationType) {
        this.affiliationType = affiliationType;
        return this;
    }

    public AffiliationResourceBuilder setExists(Boolean exists) {
        this.exists = exists;
        return this;
    }

    public AffiliationResourceBuilder setRelation(String relation) {
        this.relation = relation;
        return this;
    }

    public AffiliationResourceBuilder setOrganisation(String organisation) {
        this.organisation = organisation;
        return this;
    }

    public AffiliationResourceBuilder setPosition(String position) {
        this.position = position;
        return this;
    }

    public AffiliationResourceBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public AffiliationResource createAffiliationResource() {
        return new AffiliationResource(user, affiliationType, exists, relation, organisation, position, description);
    }
}