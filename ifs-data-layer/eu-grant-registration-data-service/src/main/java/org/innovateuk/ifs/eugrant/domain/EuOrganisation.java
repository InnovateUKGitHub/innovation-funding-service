package org.innovateuk.ifs.eugrant.domain;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.eugrant.EuOrganisationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;

/**
 * A UK Organisation that receives EU grant funding.
 */
@Entity
public class EuOrganisation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private final String name;

    @NotNull
    @Enumerated(STRING)
    private final EuOrganisationType organisationType;

    private final String companiesHouseNumber;

    EuOrganisation() {
        name = null;
        organisationType = null;
        companiesHouseNumber = null;
    }

    public EuOrganisation(final String name, final EuOrganisationType organisationType,
                          final String companiesHouseNumber) {
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
        this.companiesHouseNumber = companiesHouseNumber;
        this.name = name;
        this.organisationType = organisationType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public String getCompaniesHouseNumber() {
        return companiesHouseNumber;
    }
}