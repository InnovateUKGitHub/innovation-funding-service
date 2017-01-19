package org.innovateuk.ifs.user.resource;

import org.apache.commons.lang3.StringUtils;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;

/**
 * Builder for {@link AffiliationResource}s.
 */
public class AffiliationResourceBuilder {

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

    public static AffiliationResource createPrincipalEmployer(String principalEmployer, String role) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(EMPLOYER)
                .setExists(TRUE)
                .setOrganisation(principalEmployer)
                .setPosition(role)
                .createAffiliationResource();
    }

    public static AffiliationResource createProfessaionAffiliations(String professionalAffiliations) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(PROFESSIONAL)
                .setExists(StringUtils.isNotBlank(professionalAffiliations))
                .setDescription(professionalAffiliations)
                .createAffiliationResource();
    }

    public static AffiliationResource createAppointment(String organisation, String position) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(PERSONAL)
                .setExists(TRUE)
                .setOrganisation(organisation)
                .setPosition(position)
                .createAffiliationResource();
    }

    public static AffiliationResource createEmptyAppointments() {
        return new AffiliationResourceBuilder()
                .setAffiliationType(PERSONAL)
                .setExists(FALSE)
                .createAffiliationResource();
    }

    public static AffiliationResource createFinancialInterests(boolean exists, String financialInterests) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(PERSONAL_FINANCIAL)
                .setExists(exists)
                .setDescription(exists ? financialInterests : null)
                .createAffiliationResource();
    }

    public static AffiliationResource createFamilyAffiliation(String relation, String organisation , String position) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(FAMILY)
                .setExists(TRUE)
                .setRelation(relation)
                .setOrganisation(organisation)
                .setPosition(position)
                .createAffiliationResource();
    }

    public static AffiliationResource createEmptyFamilyAffiliations() {
        return new AffiliationResourceBuilder()
                .setAffiliationType(FAMILY)
                .setExists(FALSE)
                .createAffiliationResource();
    }

    public static AffiliationResource createFamilyFinancialInterests(boolean exists, String familyFinancialInterests) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(FAMILY_FINANCIAL)
                .setExists(exists)
                .setDescription(exists ? familyFinancialInterests : null)
                .createAffiliationResource();
    }

    public AffiliationResource createAffiliationResource() {
        return new AffiliationResource(user, affiliationType, exists, relation, organisation, position, description);
    }
}
