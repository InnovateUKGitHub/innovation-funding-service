package com.worth.ifs.project.finance.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.resource.FinanceCheckPartnerStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class FinanceCheckPartnerStatusResourceBuilder extends BaseBuilder<FinanceCheckPartnerStatusResource, FinanceCheckPartnerStatusResourceBuilder> {

    private FinanceCheckPartnerStatusResourceBuilder(List<BiConsumer<Integer, FinanceCheckPartnerStatusResource>> multiActions) {
        super(multiActions);
    }

    public static FinanceCheckPartnerStatusResourceBuilder newFinanceCheckPartnerStatusResource() {
        return new FinanceCheckPartnerStatusResourceBuilder(emptyList());
    }

    @Override
    protected FinanceCheckPartnerStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FinanceCheckPartnerStatusResource>> actions) {
        return new FinanceCheckPartnerStatusResourceBuilder(actions);
    }

    @Override
    protected FinanceCheckPartnerStatusResource createInitial() {
        return new FinanceCheckPartnerStatusResource();
    }

    public FinanceCheckPartnerStatusResourceBuilder withId(Long... ids) {
        return withArray((id, financeCheckPartnerStatusResource) -> setField("id", id, financeCheckPartnerStatusResource), ids);
    }

    public FinanceCheckPartnerStatusResourceBuilder withName(String... names) {
        return withArray((name, financeCheckPartnerStatusResource) -> setField("name", name, financeCheckPartnerStatusResource), names);
    }

    public FinanceCheckPartnerStatusResourceBuilder withEligibility(FinanceCheckPartnerStatusResource.Eligibility... eligibilitys) {
        return withArray((eligibility, financeCheckPartnerStatusResource) -> setField("eligibility", eligibility, financeCheckPartnerStatusResource), eligibilitys);
    }
}
