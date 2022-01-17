package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.AffiliationType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

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

    public AffiliationBuilder withModifiedOn(ZonedDateTime... values) {
        return withArraySetFieldByReflection("modifiedOn", values);
    }
}