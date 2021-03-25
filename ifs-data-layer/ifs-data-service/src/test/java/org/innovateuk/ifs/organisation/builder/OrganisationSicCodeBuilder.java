package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.SicCode;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class OrganisationSicCodeBuilder extends BaseBuilder<SicCode, OrganisationSicCodeBuilder>  {

    private OrganisationSicCodeBuilder(List<BiConsumer<Integer, SicCode>> multiActions) {
        super(multiActions);
    }

    public static OrganisationSicCodeBuilder newOrganisationSicCode() {
        return new OrganisationSicCodeBuilder(emptyList()).
                with(uniqueIds());
    }

    @Override
    protected OrganisationSicCodeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SicCode>> actions) {
        return new OrganisationSicCodeBuilder(actions);
    }

    @Override
    protected SicCode createInitial() {
        return new SicCode();
    }

    public OrganisationSicCodeBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public OrganisationSicCodeBuilder withSicCode(String... sicCodes) {
        return withArraySetFieldByReflection("sicCode", sicCodes);
    }

    public OrganisationSicCodeBuilder withOrganisation(Organisation... organisations) {
        return withArraySetFieldByReflection("organisation", organisations);
    }
}
