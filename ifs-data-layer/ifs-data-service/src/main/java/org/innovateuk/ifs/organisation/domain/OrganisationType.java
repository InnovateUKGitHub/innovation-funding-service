package org.innovateuk.ifs.organisation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;

import javax.persistence.*;
import java.util.List;

@Entity
public class OrganisationType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganisationType parentOrganisationType;

    @OneToMany(mappedBy="organisationType")
    private List<GrantClaimMaximum> grantClaimMaximums;

    private boolean visibleInSetup;

    public OrganisationType() {
    }

    public OrganisationType(String name, String description, OrganisationType parentOrganisationType) {
        this.name = name;
        this.description = description;
        this.parentOrganisationType = parentOrganisationType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrganisationType getParentOrganisationType() {
        return parentOrganisationType;
    }


    public List<GrantClaimMaximum> getGrantClaimMaximums() {
        return grantClaimMaximums;
    }

    public boolean getVisibleInSetup() {
        return visibleInSetup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationType that = (OrganisationType) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(description, that.description)
                .append(parentOrganisationType, that.parentOrganisationType)
                .append(grantClaimMaximums, that.grantClaimMaximums)
                .append(visibleInSetup, that.visibleInSetup)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(description)
                .append(parentOrganisationType)
                .append(grantClaimMaximums)
                .append(visibleInSetup)
                .toHashCode();
    }
}