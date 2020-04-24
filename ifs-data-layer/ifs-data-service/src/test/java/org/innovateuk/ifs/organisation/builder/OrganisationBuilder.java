package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;

/**
 * Builder for Organisation entities.
 */
public class OrganisationBuilder extends BaseBuilder<Organisation, OrganisationBuilder> {

    private OrganisationBuilder(List<BiConsumer<Integer, Organisation>> multiActions) {
        super(multiActions);
    }

    public static OrganisationBuilder newOrganisation() {
        return new OrganisationBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("Organisation "));
    }

    @Override
    protected OrganisationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Organisation>> actions) {
        return new OrganisationBuilder(actions);
    }

    @Override
    protected Organisation createInitial() {
        return new Organisation();
    }

    public OrganisationBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public OrganisationBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public OrganisationBuilder withCompaniesHouseNumber(String... numbers) {
        return withArraySetFieldByReflection("companiesHouseNumber", numbers);
    }

    public OrganisationBuilder withOrganisationType(OrganisationType... organisationTypes) {
        return withArraySetFieldByReflection("organisationType", organisationTypes);
    }

    public OrganisationBuilder withOrganisationType(OrganisationTypeEnum... type) {
        return withOrganisationType(newOrganisationType().withOrganisationType(type).buildArray(type.length, OrganisationType.class));
    }

    public OrganisationBuilder withAddress(List<OrganisationAddress> addresses) {
        return withList(addresses, (addressList, org) ->  org.setAddresses(addresses));
    }
}