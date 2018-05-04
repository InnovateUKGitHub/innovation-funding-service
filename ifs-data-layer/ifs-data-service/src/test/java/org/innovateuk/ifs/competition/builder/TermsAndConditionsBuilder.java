package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.TermsAndConditions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class TermsAndConditionsBuilder extends BaseBuilder<TermsAndConditions, TermsAndConditionsBuilder> {

    private TermsAndConditionsBuilder(List<BiConsumer<Integer, TermsAndConditions>> newMultiActions) {
        super(newMultiActions);
    }

    public static TermsAndConditionsBuilder newTermsAndConditions() {
        return new TermsAndConditionsBuilder(emptyList()).with(uniqueIds());
    }

    public TermsAndConditionsBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public TermsAndConditionsBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public TermsAndConditionsBuilder withTemplate(String... templates) {
        return withArray((template, object) -> setField("template", template, object), templates);
    }

    public TermsAndConditionsBuilder withVersion(String... versions) {
        return withArray((version, object) -> setField("version", version, object), versions);
    }

    @Override
    protected TermsAndConditionsBuilder createNewBuilderWithActions(List<BiConsumer<Integer, TermsAndConditions>>actions) {
        return new TermsAndConditionsBuilder(actions);
    }

    @Override
    protected TermsAndConditions createInitial() {
        return new TermsAndConditions();
    }
}
