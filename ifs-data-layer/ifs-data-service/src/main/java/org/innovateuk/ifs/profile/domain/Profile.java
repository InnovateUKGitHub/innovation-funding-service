package org.innovateuk.ifs.profile.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ProfileInnovationAreaLink;
import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.BusinessType;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link User}'s profile with their {@link Address}, skills areas and signed {@link Agreement}.
 */
@Entity
public class Profile extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(targetEntity = Address.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "skills_areas")
    private String skillsAreas;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProfileInnovationAreaLink> innovationAreas = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @ManyToOne(targetEntity = Agreement.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id")
    private Agreement agreement;

    private ZonedDateTime agreementSignedDate;

    public Profile() {
        // no-arg constructor
    }

    public void signAgreement(Agreement agreement, ZonedDateTime signedDate) {
        if (agreement == null) {
            throw new NullPointerException("agreement cannot be null");
        }
        if (signedDate == null) {
            throw new NullPointerException("signedDate cannot be null");
        }
        this.agreement = agreement;
        this.agreementSignedDate = signedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getSkillsAreas() {
        return skillsAreas;
    }

    public void setSkillsAreas(String skillsAreas) {
        this.skillsAreas = skillsAreas;
    }

    public Set<InnovationArea> getInnovationAreas() {
        return innovationAreas.stream().map(ProfileInnovationAreaLink::getCategory).collect(Collectors.toSet());
    }

    public void addInnovationArea(InnovationArea innovationArea) {
        if (innovationArea == null) {
            throw new NullPointerException("innovationArea cannot be null");
        }
        innovationAreas.add(new ProfileInnovationAreaLink(this, innovationArea));
    }

    public void addInnovationAreas(Set<InnovationArea> innovationAreas) {
        innovationAreas.forEach(this::addInnovationArea);
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    public ZonedDateTime getAgreementSignedDate() {
        return agreementSignedDate;
    }

    public void setAgreementSignedDate(ZonedDateTime agreementSignedDate) {
        this.agreementSignedDate = agreementSignedDate;
    }

    // TODO the profile belongs to a User so should the User be a member?
    public boolean isCompliant(User user) {
        boolean skillsComplete = skillsAreas != null;
        boolean affiliationsComplete = user != null && user.getAffiliations() != null
                && !user.getAffiliations().isEmpty();
        boolean agreementComplete = agreementSignedDate != null;
        return skillsComplete && affiliationsComplete && agreementComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Profile profile = (Profile) o;

        return new EqualsBuilder()
                .append(id, profile.id)
                .append(address, profile.address)
                .append(skillsAreas, profile.skillsAreas)
                .append(innovationAreas, profile.innovationAreas)
                .append(businessType, profile.businessType)
                .append(agreement, profile.agreement)
                .append(agreementSignedDate, profile.agreementSignedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(address)
                .append(skillsAreas)
                .append(innovationAreas)
                .append(businessType)
                .append(agreement)
                .append(agreementSignedDate)
                .toHashCode();
    }
}
