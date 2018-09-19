package org.innovateuk.ifs.eugrant.domain;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.eugrant.EuOrganisationType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuOrganisationBuilder extends BaseBuilder<EuOrganisation, EuOrganisationBuilder> {

    private EuOrganisationBuilder(List<BiConsumer<Integer, EuOrganisation>> multiActions) {
        super(multiActions);
    }

    public static EuOrganisationBuilder newEuOrganisation() {
        return new EuOrganisationBuilder(emptyList());
    }

    @Override
    protected EuOrganisationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuOrganisation>> actions) {
        return new EuOrganisationBuilder(actions);
    }

    @Override
    protected EuOrganisation createInitial() {
        return new EuOrganisation();
    }

    public EuOrganisationBuilder withName(String... names) {
        return withArray((name, organisation) -> organisation.setName(name), names);
    }

    public EuOrganisationBuilder withOrganisationType(EuOrganisationType... organisationTypes) {
        return withArray((organisationType, organisation) -> organisation.setOrganisationType(organisationType), organisationTypes);
    }

    public EuOrganisationBuilder withCompaniesHouseNumber(String... companiesHouseNumbers) {
        return withArray((companiesHouseNumber, organisation) -> organisation.setCompaniesHouseNumber(companiesHouseNumber), companiesHouseNumbers);
    }
}
