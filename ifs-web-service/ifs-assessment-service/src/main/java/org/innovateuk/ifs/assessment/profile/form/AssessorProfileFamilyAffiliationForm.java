package org.innovateuk.ifs.assessment.profile.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Form field model for the Assessor Profile Declaration of Interest Family affiliations/Appointments, directorships or consultancies
 */
public class AssessorProfileFamilyAffiliationForm {
    public interface FamilyAffiliations {
    }

    @NotBlank(message = "{validation.assessorprofilefamilyaffiliationform.relation.required}", groups=FamilyAffiliations.class)
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String relation;
    @NotBlank(message = "{validation.assessorprofilefamilyaffiliationform.organisation.required}", groups=FamilyAffiliations.class)
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    private String organisation;
    @Size(max = 255, message = "{validation.field.too.many.characters}")
    @NotBlank(message = "{validation.assessorprofilefamilyaffiliationform.position.required}", groups=FamilyAffiliations.class)
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
