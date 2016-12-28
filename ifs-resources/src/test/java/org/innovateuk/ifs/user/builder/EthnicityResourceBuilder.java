package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.user.resource.EthnicityResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link org.innovateuk.ifs.user.resource.EthnicityResource}.
 */
public class EthnicityResourceBuilder extends BaseBuilder<EthnicityResource, EthnicityResourceBuilder> {

    private EthnicityResourceBuilder(List<BiConsumer<Integer, EthnicityResource>> newActions) {
        super(newActions);
    }

    public static EthnicityResourceBuilder newEthnicityResource() {
        return new EthnicityResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected EthnicityResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EthnicityResource>> actions) {
        return new EthnicityResourceBuilder(actions);
    }

    @Override
    protected EthnicityResource createInitial() {
        return new EthnicityResource();
    }

    public EthnicityResourceBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public EthnicityResourceBuilder withName(String... names) {
        return withArray((name, ethnicityResource) -> setField("name", name, ethnicityResource), names);
    }

    public EthnicityResourceBuilder withDescription(String... descriptions) {
        return withArray((description, ethnicityResource) -> setField("description", description, ethnicityResource), descriptions);
    }

    public EthnicityResourceBuilder withPriority(Integer... priorities) {
        return withArray((priority, ethnicityResource) -> setField("priority", priority, ethnicityResource), priorities);
    }
}
