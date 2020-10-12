package org.innovateuk.ifs.cofunder.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.cofunder.domain.CofunderOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class CofunderOutcomeBuilder
        extends BaseBuilder<CofunderOutcome, CofunderOutcomeBuilder> {

    private CofunderOutcomeBuilder(List<BiConsumer<Integer, CofunderOutcome>> multiActions) {
        super(multiActions);
    }

    public static CofunderOutcomeBuilder newCofunderOutcome() {
        return new CofunderOutcomeBuilder(emptyList());
    }

    @Override
    protected CofunderOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            CofunderOutcome>> actions) {
        return new CofunderOutcomeBuilder(actions);
    }

    @Override
    protected CofunderOutcome createInitial() {
        return new CofunderOutcome();
    }

    public CofunderOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public CofunderOutcomeBuilder withFundingConfirmation(Boolean... fundingConfirmations) {
        return withArray((value, CofunderOutcomeBuilder) -> CofunderOutcomeBuilder.setFundingConfirmation(value), fundingConfirmations);
    }

    public CofunderOutcomeBuilder withComment(String... comments) {
        return withArray((value, CofunderOutcomeBuilder) -> CofunderOutcomeBuilder.setComment(value), comments);
    }

}