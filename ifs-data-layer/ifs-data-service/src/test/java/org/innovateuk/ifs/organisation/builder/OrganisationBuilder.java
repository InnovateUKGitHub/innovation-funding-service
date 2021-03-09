package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.domain.*;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.time.LocalDate;
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

    public OrganisationBuilder withInternationalRegistrationNumber(String... internationalRegistrationNumbers) {
        return withArraySetFieldByReflection("internationalRegistrationNumber", internationalRegistrationNumbers);
    }

    public OrganisationBuilder withInternational(boolean... internationals) {
        return withArraySetFieldByReflection("international", internationals);
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

    public OrganisationBuilder withDateOfIncorporation(LocalDate... dateOfIncorporations) {
        return withArraySetFieldByReflection("dateOfIncorporation", dateOfIncorporations);
    }

    public OrganisationBuilder withSicCodes(List<SicCode>... sicCodes) {
        return withArraySetFieldByReflection("sicCodes", sicCodes);
    }

    public OrganisationBuilder withExecutiveOfficers(List<ExecutiveOfficer>... executiveOfficers) {
        return withArraySetFieldByReflection("executiveOfficers", executiveOfficers);
    }

    public OrganisationBuilder withAddresses(List<OrganisationAddress>... organisationAddresses) {
        return withArraySetFieldByReflection("addresses", organisationAddresses);
    }
}