package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

/**
 * Abstract DTO for fields common to assessor invite resources.
 */
public abstract class AssessorInviteResource {

    private Long id;
    private String name;
    private List<InnovationAreaResource> innovationAreas;
    private boolean compliant;
    private boolean validAgreement;
    private boolean validDoi;


    protected AssessorInviteResource() {
    }

    protected AssessorInviteResource(Long id,
                                     String name,
                                     List<InnovationAreaResource> innovationAreas,
                                     boolean compliant,
                                     boolean validAgreement,
                                     boolean validDoi) {
        this.id = id;
        this.name = name;
        this.innovationAreas = innovationAreas;
        this.compliant = compliant;
        this.validAgreement = validAgreement;
        this.validDoi = validDoi;
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

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }

    public boolean isCompliant() {
        return compliant;
    }

    public void setCompliant(boolean compliant) {
        this.compliant = compliant;
    }

    public boolean isValidAgreement() {
        return validAgreement;
    }

    public void setValidAgreement(boolean validAgreement) {
        this.validAgreement = validAgreement;
    }

    public boolean isValidDoi() {
        return validDoi;
    }

    public void setValidDoi(boolean validDoi) {
        this.validDoi = validDoi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorInviteResource that = (AssessorInviteResource) o;

        return new EqualsBuilder()
                .append(compliant, that.compliant)
                .append(validAgreement, that.validAgreement)
                .append(validDoi, that.validDoi)
                .append(id, that.id)
                .append(name, that.name)
                .append(innovationAreas, that.innovationAreas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(innovationAreas)
                .append(compliant)
                .append(validAgreement)
                .append(validDoi)
                .toHashCode();
    }
}