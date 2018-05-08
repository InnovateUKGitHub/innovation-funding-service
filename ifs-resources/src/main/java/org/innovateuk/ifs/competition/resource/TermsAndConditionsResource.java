package org.innovateuk.ifs.competition.resource;

import java.time.ZonedDateTime;

/**
 * Resource representation of TermsAndConditions
 */
public abstract class TermsAndConditionsResource {

    private Long id;
    private String name;
    private String template;
    private int version;
    private String createdBy;
    private ZonedDateTime createdOn;
    private String modifiedBy;
    private ZonedDateTime modifiedOn;

    public TermsAndConditionsResource() {
    }

    public TermsAndConditionsResource(String name, String template, int version) {
        this.name = name;
        this.template = template;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(final String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ZonedDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(final ZonedDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
