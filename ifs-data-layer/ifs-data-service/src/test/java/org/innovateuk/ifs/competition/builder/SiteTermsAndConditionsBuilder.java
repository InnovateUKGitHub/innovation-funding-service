package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.domain.SiteTermsAndConditions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class SiteTermsAndConditionsBuilder extends AbstractVersionedTemplateBuilder<SiteTermsAndConditions,
        SiteTermsAndConditionsBuilder> {

    private SiteTermsAndConditionsBuilder(List<BiConsumer<Integer, SiteTermsAndConditions>> multiActions) {
        super(multiActions);
    }

    @Override
    protected SiteTermsAndConditionsBuilder createNewBuilderWithActions(final List<BiConsumer<Integer,
            SiteTermsAndConditions>> actions) {
        return new SiteTermsAndConditionsBuilder(actions);
    }

    public static SiteTermsAndConditionsBuilder newSiteTermsAndConditions() {
        return new SiteTermsAndConditionsBuilder(emptyList())
                .with(uniqueIds());
    }

    @Override
    protected SiteTermsAndConditions createInitial() {
        return new SiteTermsAndConditions();
    }
}
