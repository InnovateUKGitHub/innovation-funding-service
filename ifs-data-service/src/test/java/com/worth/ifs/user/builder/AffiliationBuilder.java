package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Affiliation;
import com.worth.ifs.user.resource.AffiliationType;
import com.worth.ifs.user.domain.User;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link Affiliation}s.
 */
public class AffiliationBuilder extends BaseBuilder<Affiliation, AffiliationBuilder> {

    private AffiliationBuilder(List<BiConsumer<Integer, Affiliation>> multiActions) {
        super(multiActions);
    }

    public static AffiliationBuilder newAffiliation() {
        return new AffiliationBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AffiliationBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Affiliation>> actions) {
        return new AffiliationBuilder(actions);
    }

    @Override
    protected Affiliation createInitial() {
        return createDefault(Affiliation.class);
    }

    public AffiliationBuilder withId(Long... ids) {
        return withArray((id, affiliationResource) -> setField("id", id, affiliationResource), ids);
    }

    public AffiliationBuilder withUser(User... users) {
        return withArray((user, affiliationResource) -> setField("user", user, affiliationResource), users);
    }

    public AffiliationBuilder withAffiliationType(AffiliationType... affiliationTypes) {
        return withArray((affiliationType, affiliationResource) -> setField("affiliationType", affiliationType, affiliationResource), affiliationTypes);
    }

    public AffiliationBuilder withExists(Boolean... existss) {
        return withArray((exists, affiliationResource) -> setField("exists", exists, affiliationResource), existss);
    }

    public AffiliationBuilder withRelation(String... relations) {
        return withArray((relation, affiliationResource) -> setField("relation", relation, affiliationResource), relations);
    }

    public AffiliationBuilder withOrganisation(String... organisations) {
        return withArray((organisation, affiliationResource) -> setField("organisation", organisation, affiliationResource), organisations);
    }

    public AffiliationBuilder withPosition(String... positions) {
        return withArray((position, affiliationResource) -> setField("position", position, affiliationResource), positions);
    }

    public AffiliationBuilder withDescription(String... descriptions) {
        return withArray((description, affiliationResource) -> setField("description", description, affiliationResource), descriptions);
    }
}