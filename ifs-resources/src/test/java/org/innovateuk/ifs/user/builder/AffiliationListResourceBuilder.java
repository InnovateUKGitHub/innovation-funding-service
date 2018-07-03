package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link org.innovateuk.ifs.user.resource.AffiliationListResource}s.
 */
public class AffiliationListResourceBuilder extends BaseBuilder<AffiliationListResource, AffiliationListResourceBuilder> {

    private AffiliationListResourceBuilder(List<BiConsumer<Integer, AffiliationListResource>> multiActions) {
        super(multiActions);
    }

    public static AffiliationListResourceBuilder newAffiliationListResource() {
        return new AffiliationListResourceBuilder(emptyList());
    }

    @Override
    protected AffiliationListResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AffiliationListResource>> actions) {
        return new AffiliationListResourceBuilder(actions);
    }

    @Override
    protected AffiliationListResource createInitial() {
        return new AffiliationListResource();
    }

    public AffiliationListResourceBuilder withAffiliationList(List<AffiliationResource>... affiliationResources) {
        return withArraySetFieldByReflection("affiliationResourceList", affiliationResources);
    }
}
