package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class SiteTermsAndConditionsResourceBuilder extends
        AbstractTermsAndConditionsResourceBuilder<SiteTermsAndConditionsResource, SiteTermsAndConditionsResourceBuilder> {

    private SiteTermsAndConditionsResourceBuilder(List<BiConsumer<Integer, SiteTermsAndConditionsResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected SiteTermsAndConditionsResourceBuilder createNewBuilderWithActions(final List<BiConsumer<Integer,
            SiteTermsAndConditionsResource>> actions) {
        return new SiteTermsAndConditionsResourceBuilder(actions);
    }

    @Override
    protected SiteTermsAndConditionsResource createInitial() {
        return new SiteTermsAndConditionsResource();
    }

    public static SiteTermsAndConditionsResourceBuilder newSiteTermsAndConditionsResource() {
        return new SiteTermsAndConditionsResourceBuilder(emptyList()).with(uniqueIds());
    }
}