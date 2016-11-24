package com.worth.ifs.assessment.form.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form field model for the Assessor Profile Declaration of Interest Family affiliations/Appointments, directorships or consultancies
 */
public class AssessorProfileFamilyAffiliationForm {
    public interface FamilyAffiliations {
    }

    @NotEmpty(message = "{validation.assessorprofilefamilyaffiliationform.relation.required}", groups=FamilyAffiliations.class)
    private String relation;
    @NotEmpty(message = "{validation.assessorprofilefamilyaffiliationform.organisation.required}", groups=FamilyAffiliations.class)
    private String organisation;
    @NotEmpty(message = "{validation.assessorprofilefamilyaffiliationform.position.required}", groups=FamilyAffiliations.class)
    private String position;

    public AssessorProfileFamilyAffiliationForm() {
    }

    public AssessorProfileFamilyAffiliationForm(String relation, String organisation, String position) {
        this.relation = relation;
        this.organisation = organisation;
        this.position = position;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileFamilyAffiliationForm that = (AssessorProfileFamilyAffiliationForm) o;

        return new EqualsBuilder()
                .append(relation, that.relation)
                .append(organisation, that.organisation)
                .append(position, that.position)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(relation)
                .append(organisation)
                .append(position)
                .toHashCode();
    }
}
