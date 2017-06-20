package org.innovateuk.ifs.finance.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.domain.OrganisationSize;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.BuilderAmendFunctions.uniqueIds;

/**
 * Builder for OrganisationSize entities.
 */
public class OrganisationSizeBuilder extends BaseBuilder<OrganisationSize, OrganisationSizeBuilder> {

    public OrganisationSizeBuilder withId(Long... value) {
        return withArray((v, size) -> size.setId(v), value);
    }

    public OrganisationSizeBuilder withDescription(String... value) {
        return withArray((v, size) -> size.setDescription(v), value);
    }

    private OrganisationSizeBuilder(List<BiConsumer<Integer, OrganisationSize>> newMultiActions) {
        super(newMultiActions);
    }

    public static OrganisationSizeBuilder newOrganisationSize() {
        return new OrganisationSizeBuilder(emptyList()).
                with(uniqueIds());
    }

    @Override
    protected OrganisationSizeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, OrganisationSize>> actions) {
        return new OrganisationSizeBuilder(actions);
    }

    @Override
    protected OrganisationSize createInitial() {
        return new OrganisationSize();
    }
}
