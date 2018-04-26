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

    public TermsAndConditionsResourceBuilder withId(Long id) {
        return with(termsAndConditions -> setField("id", id, termsAndConditions));
    }

    public TermsAndConditionsResourceBuilder withName(String name) {
        return with(termsAndConditions -> setField("name", name, termsAndConditions));
    }

    public TermsAndConditionsResourceBuilder withTemplate(String template) {
        return with(termsAndConditions -> setField("template", template, termsAndConditions));
    }

    public TermsAndConditionsResourceBuilder withVersion(String version) {
        return with(termsAndConditions -> setField("version", version, termsAndConditions));
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
