package org.innovateuk.ifs.supporter.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.supporter.domain.SupporterOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class SupporterOutcomeBuilder
        extends BaseBuilder<SupporterOutcome, SupporterOutcomeBuilder> {

    private SupporterOutcomeBuilder(List<BiConsumer<Integer, SupporterOutcome>> multiActions) {
        super(multiActions);
    }

    public static SupporterOutcomeBuilder newSupporterOutcome() {
        return new SupporterOutcomeBuilder(emptyList());
    }

    @Override
    protected SupporterOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            SupporterOutcome>> actions) {
        return new SupporterOutcomeBuilder(actions);
    }

    @Override
    protected SupporterOutcome createInitial() {
        return new SupporterOutcome();
    }

    public SupporterOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public SupporterOutcomeBuilder withFundingConfirmation(Boolean... fundingConfirmations) {
        return withArray((value, SupporterOutcomeBuilder) -> SupporterOutcomeBuilder.setFundingConfirmation(value), fundingConfirmations);
    }

    public SupporterOutcomeBuilder withComment(String... comments) {
        return withArray((value, SupporterOutcomeBuilder) -> SupporterOutcomeBuilder.setComment(value), comments);
    }

}