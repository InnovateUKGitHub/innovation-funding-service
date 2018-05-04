package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class TermsAndConditionsResourceBuilder extends BaseBuilder<TermsAndConditionsResource, TermsAndConditionsResourceBuilder> {

    private TermsAndConditionsResourceBuilder(List<BiConsumer<Integer, TermsAndConditionsResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static TermsAndConditionsResourceBuilder newTermsAndConditionsResource() {
        return new TermsAndConditionsResourceBuilder(emptyList()).with(uniqueIds());
    }

    public TermsAndConditionsResourceBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public TermsAndConditionsResourceBuilder withName(String... names) {
        return withArray((name, object) -> setField("name", name, object), names);
    }

    public TermsAndConditionsResourceBuilder withTemplate(String... templates) {
        return withArray((template, object) -> setField("template", template, object), templates);
    }

    public TermsAndConditionsResourceBuilder withVersion(String... versions) {
        return withArray((version, object) -> setField("version", version, object), versions);
    }

    @Override
    protected TermsAndConditionsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, TermsAndConditionsResource>> actions) {
        return new TermsAndConditionsResourceBuilder(actions);
    }

    @Override
    protected TermsAndConditionsResource createInitial() {
        return new TermsAndConditionsResource();
    }
}
