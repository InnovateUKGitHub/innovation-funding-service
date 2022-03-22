package org.innovateuk.ifs.competition.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Resource representation of GrantTermsAndConditions
 */
public class GrantTermsAndConditionsResource extends VersionedTemplateResource {
    private static final String PROCUREMENT = "Procurement";
    private static final String THIRD_PARTY = "Third Party";

    public GrantTermsAndConditionsResource() {
    }

    public GrantTermsAndConditionsResource(String name, String template, int version) {
        super(name, template, version);
    }

    @JsonIgnore
    public boolean isProcurement() {
        return name.equals(PROCUREMENT);
    }

    @JsonIgnore
    public boolean isProcurementThirdParty() {
        return name.equals(THIRD_PARTY);
    }
}
