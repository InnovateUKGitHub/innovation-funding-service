package org.innovateuk.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Resource representation of GrantTermsAndConditions
 */
public class GrantTermsAndConditionsResource extends VersionedTemplateResource {

    public GrantTermsAndConditionsResource() {
    }

    public GrantTermsAndConditionsResource(String name, String template, int version) {
        super(name, template, version);
    }

    @JsonIgnore
    public boolean isProcurement() {
        return name.equals("Procurement");
    }
}
