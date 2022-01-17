package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.project.finance.resource.FundingRulesResource;
import org.innovateuk.ifs.project.finance.resource.FundingRulesState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class FundingRulesResourceBuilder extends BaseBuilder<FundingRulesResource, FundingRulesResourceBuilder> {

    public FundingRulesResourceBuilder withFundingRules(FundingRules... fundingRuleses) {
        return withArray((fundingRules, fundingRulesResource) -> fundingRulesResource.setFundingRules(fundingRules), fundingRuleses);
    }

    public FundingRulesResourceBuilder withFundingRulesState(FundingRulesState... fundingRulesStates) {
        return withArray((fundingRulesState, fundingRulesResource) -> fundingRulesResource.setFundingRulesState(fundingRulesState), fundingRulesStates);
    }

    private FundingRulesResourceBuilder(List<BiConsumer<Integer, FundingRulesResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static FundingRulesResourceBuilder newFundingRulesResource() {
        return new FundingRulesResourceBuilder(emptyList());
    }

    @Override
    protected FundingRulesResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FundingRulesResource>> actions) {
        return new FundingRulesResourceBuilder(actions);
    }

    @Override
    protected FundingRulesResource createInitial() {
        return new FundingRulesResource();
    }


}
