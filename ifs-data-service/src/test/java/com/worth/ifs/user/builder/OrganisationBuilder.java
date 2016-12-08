package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationTypeEnum;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static java.util.Collections.emptyList;

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

    public OrganisationBuilder withCompanyHouseNumber(String... numbers) {
        return withArraySetFieldByReflection("companyHouseNumber", numbers);
    }

    public OrganisationBuilder withOrganisationType(OrganisationType... organisationTypes) {
        return withArraySetFieldByReflection("organisationType", organisationTypes);
    }

    public OrganisationBuilder withOrganisationSize(OrganisationSize... organisationSize) {
        return withArraySetFieldByReflection("organisationSize", organisationSize);
    }

    public OrganisationBuilder withOrganisationType(OrganisationTypeEnum type) {
        return withOrganisationType(newOrganisationType().withOrganisationType(type).build());
    }
}
