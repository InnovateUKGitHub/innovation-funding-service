package org.innovateuk.ifs.heukar.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "heukar_org_type", schema = "ifs", catalog = "")
public class HeukarOrganisationType {
    private long id;
    private Long applicationId;
    private Long organisationTypeId;

    public HeukarOrganisationType(long id, Long applicationId, Long organisationTypeId) {
        this.id = id;
        this.applicationId = applicationId;
        this.organisationTypeId = organisationTypeId;
    }

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "application_id")
    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    @Basic
    @Column(name = "organisation_type_id")
    public Long getOrganisationTypeId() {
        return organisationTypeId;
    }

    public void setOrganisationTypeId(Long organisationTypeId) {
        this.organisationTypeId = organisationTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeukarOrganisationType that = (HeukarOrganisationType) o;
        return id == that.id &&
                Objects.equals(applicationId, that.applicationId) &&
                Objects.equals(organisationTypeId, that.organisationTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, organisationTypeId);
    }

}
