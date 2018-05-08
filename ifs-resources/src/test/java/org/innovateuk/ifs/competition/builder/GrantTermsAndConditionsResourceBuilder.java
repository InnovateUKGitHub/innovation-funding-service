package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class GrantTermsAndConditionsResourceBuilder extends
        AbstractTermsAndConditionsResourceBuilder<GrantTermsAndConditionsResource,
                GrantTermsAndConditionsResourceBuilder> {

    private GrantTermsAndConditionsResourceBuilder(List<BiConsumer<Integer, GrantTermsAndConditionsResource>>
                                                           newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected GrantTermsAndConditionsResourceBuilder createNewBuilderWithActions(final List<BiConsumer<Integer,
            GrantTermsAndConditionsResource>> actions) {
        return new GrantTermsAndConditionsResourceBuilder(actions);
    }

    @Override
    protected GrantTermsAndConditionsResource createInitial() {
        return new GrantTermsAndConditionsResource();
    }

    public static GrantTermsAndConditionsResourceBuilder newGrantTermsAndConditionsResource() {
        return new GrantTermsAndConditionsResourceBuilder(emptyList()).with(uniqueIds());
    }
}