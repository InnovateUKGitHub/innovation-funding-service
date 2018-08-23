package org.innovateuk.ifs.eugrant.domain;

import org.innovateuk.ifs.eugrant.EuOrganisationType;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
public class EuOrganisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private final String name;
    @OneToOne
    private final EuAddress address;
    @Enumerated(STRING)
    private final EuOrganisationType organisationType;
    private final String companiesHouseNumber;

    public EuOrganisation(final String name, final EuOrganisationType organisationType,
                          final String companiesHouseNumber, final EuAddress address) {
        if (name == null) {
            throw new NullPointerException("name connect be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        if (organisationType == null) {
            throw new NullPointerException("organisationType cannot be null");
        }
        if (companiesHouseNumber == null) {
            throw new NullPointerException("companiesHouseNumber cannot be null");
        }
        if (companiesHouseNumber.isEmpty()) {
            throw new IllegalArgumentException("companiesHouseNumber cannot be empty");
        }
        if (address == null) {
            throw new NullPointerException("address cannot be null");
        }
        this.companiesHouseNumber = companiesHouseNumber;
        this.name = name;
        this.organisationType = organisationType;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EuAddress getAddress() {
        return address;
    }

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public String getCompaniesHouseNumber() {
        return companiesHouseNumber;
    }
}