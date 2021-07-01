package org.innovateuk.ifs.organisation.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.organisation.domain.SimpleOrganisation;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedNames;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for Organisation entities.
 */
public class SimpleOrganisationBuilder extends BaseBuilder<SimpleOrganisation, SimpleOrganisationBuilder> {

    private SimpleOrganisationBuilder(List<BiConsumer<Integer, SimpleOrganisation>> multiActions) {
        super(multiActions);
    }

    public static SimpleOrganisationBuilder newSimpleOrganisation() {
        return new SimpleOrganisationBuilder(emptyList()).
                with(uniqueIds()).
                with(idBasedNames("Organisation "));
    }

    @Override
    protected SimpleOrganisationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SimpleOrganisation>> actions) {
        return new SimpleOrganisationBuilder(actions);
    }

    @Override
    protected SimpleOrganisation createInitial() {
        return newInstance(SimpleOrganisation.class);
    }

    public SimpleOrganisationBuilder withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public SimpleOrganisationBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }
}