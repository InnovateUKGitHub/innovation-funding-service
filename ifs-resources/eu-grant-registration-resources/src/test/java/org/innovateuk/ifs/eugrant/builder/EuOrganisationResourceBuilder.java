package org.innovateuk.ifs.eugrant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.EuOrganisationType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuOrganisationResourceBuilder extends BaseBuilder<EuOrganisationResource, EuOrganisationResourceBuilder> {

    private EuOrganisationResourceBuilder(List<BiConsumer<Integer, EuOrganisationResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static EuOrganisationResourceBuilder newEuOrganisationResource() {
        return new EuOrganisationResourceBuilder(emptyList());
    }

    @Override
    protected EuOrganisationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuOrganisationResource>> actions) {
        return new EuOrganisationResourceBuilder(actions);
    }

    @Override
    protected EuOrganisationResource createInitial() {
        return new EuOrganisationResource();
    }

    public EuOrganisationResourceBuilder withName(String... names) {
        return withArray((name, organisation) -> organisation.setName(name), names);
    }

    public EuOrganisationResourceBuilder withOrganisationType(EuOrganisationType... organisationTypes) {
        return withArray((organisationType, organisation) -> organisation.setOrganisationType(organisationType), organisationTypes);
    }

    public EuOrganisationResourceBuilder withCompaniesHouseNumber(String... companiesHouseNumbers) {
        return withArray((companiesHouseNumber, organisation) -> organisation.setCompaniesHouseNumber(companiesHouseNumber), companiesHouseNumbers);
    }
}
