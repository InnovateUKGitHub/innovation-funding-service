package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.TermsAndConditions;

import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.util.Lists.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class TermsAndConditionsBuilder extends BaseBuilder<TermsAndConditions, TermsAndConditionsBuilder>  {

    private TermsAndConditionsBuilder(List<BiConsumer<Integer, TermsAndConditions>> newMultiActions) {
        super(newMultiActions);
    }

    public static TermsAndConditionsBuilder newTermsAndConditions() {
        return new TermsAndConditionsBuilder(emptyList()).with(uniqueIds());
    }

    public TermsAndConditionsBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    @Override
    protected TermsAndConditionsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, TermsAndConditions>> actions) {
        return new TermsAndConditionsBuilder(actions);
    }

    @Override
    protected TermsAndConditions createInitial() {
        return createDefault(TermsAndConditions.class);
    }
}
