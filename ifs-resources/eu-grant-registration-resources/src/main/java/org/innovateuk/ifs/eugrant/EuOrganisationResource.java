package org.innovateuk.ifs.eugrant;

public class EuOrganisationResource {

    private String name;
    private EuOrganisationType organisationType;
    private String companiesHouseNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EuOrganisationType getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(EuOrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public String getCompaniesHouseNumber() {
        return companiesHouseNumber;
    }

    public void setCompaniesHouseNumber(String companiesHouseNumber) {
        this.companiesHouseNumber = companiesHouseNumber;
    }
}
