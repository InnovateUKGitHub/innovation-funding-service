package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.AffiliationType;
import com.worth.ifs.user.resource.AffiliationResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link com.worth.ifs.user.resource.AffiliationResource}s.
 */
public class AffiliationResourceBuilder extends BaseBuilder<AffiliationResource, AffiliationResourceBuilder> {

    private AffiliationResourceBuilder(List<BiConsumer<Integer, AffiliationResource>> multiActions) {
        super(multiActions);
    }

    public static AffiliationResourceBuilder newAffiliationResource() {
        return new AffiliationResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AffiliationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AffiliationResource>> actions) {
        return new AffiliationResourceBuilder(actions);
    }

    @Override
    protected AffiliationResource createInitial() {
        return createDefault(AffiliationResource.class);
    }

    public AffiliationResourceBuilder withId(Long... ids) {
        return withArray((id, affiliationResource) -> setField("id", id, affiliationResource), ids);
    }

    public AffiliationResourceBuilder withUser(Long... users) {
        return withArray((user, affiliationResource) -> setField("user", user, affiliationResource), users);
    }

    public AffiliationResourceBuilder withAffiliationType(AffiliationType... affiliationTypes) {
        return withArray((affiliationType, affiliationResource) -> setField("affiliationType", affiliationType, affiliationResource), affiliationTypes);
    }

    public AffiliationResourceBuilder withExists(Boolean... existss) {
        return withArray((exists, affiliationResource) -> setField("exists", exists, affiliationResource), existss);
    }

    public AffiliationResourceBuilder withRelation(String... relations) {
        return withArray((relation, affiliationResource) -> setField("relation", relation, affiliationResource), relations);
    }

    public AffiliationResourceBuilder withOrganisation(String... organisations) {
        return withArray((organisation, affiliationResource) -> setField("organisation", organisation, affiliationResource), organisations);
    }

    public AffiliationResourceBuilder withPosition(String... positions) {
        return withArray((position, affiliationResource) -> setField("position", position, affiliationResource), positions);
    }

    public AffiliationResourceBuilder withDescription(String... descriptions) {
        return withArray((description, affiliationResource) -> setField("description", description, affiliationResource), descriptions);
    }

}