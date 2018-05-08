package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.domain.GrantTermsAndConditions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class GrantTermsAndConditionsBuilder extends AbstractTermsAndConditionsBuilder<GrantTermsAndConditions,
        GrantTermsAndConditionsBuilder> {

    private GrantTermsAndConditionsBuilder(List<BiConsumer<Integer, GrantTermsAndConditions>> multiActions) {
        super(multiActions);
    }

    @Override
    protected GrantTermsAndConditionsBuilder createNewBuilderWithActions(final List<BiConsumer<Integer,
            GrantTermsAndConditions>> actions) {
        return new GrantTermsAndConditionsBuilder(actions);
    }

    @Override
    protected GrantTermsAndConditions createInitial() {
        return new GrantTermsAndConditions();
    }

    public static GrantTermsAndConditionsBuilder newGrantTermsAndConditions() {
        return new GrantTermsAndConditionsBuilder(emptyList())
                .with(uniqueIds());
    }
}
