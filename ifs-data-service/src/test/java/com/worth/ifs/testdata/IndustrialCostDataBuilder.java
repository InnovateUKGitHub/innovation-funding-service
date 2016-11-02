package com.worth.ifs.testdata;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;


public class IndustrialCostDataBuilder extends BaseDataBuilder<IndustrialCostData, IndustrialCostDataBuilder> {

    public IndustrialCostDataBuilder withApplicationFinance(ApplicationFinanceResource applicationFinance) {
        return with(data -> data.setApplicationFinance(applicationFinance));
    }

    public IndustrialCostDataBuilder withLabourEntry(BigDecimal amount) {
        return with(data -> {

        });
    }

    public static IndustrialCostDataBuilder newIndustrialCostData(ServiceLocator serviceLocator) {
        return new IndustrialCostDataBuilder(emptyList(), serviceLocator);
    }

    private IndustrialCostDataBuilder(List<BiConsumer<Integer, IndustrialCostData>> multiActions,
                                      ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected IndustrialCostDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, IndustrialCostData>> actions) {
        return new IndustrialCostDataBuilder(actions, serviceLocator);
    }

    @Override
    protected IndustrialCostData createInitial() {
        return new IndustrialCostData();
    }
}
