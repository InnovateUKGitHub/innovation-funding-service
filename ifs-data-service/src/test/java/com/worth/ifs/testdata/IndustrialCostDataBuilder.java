package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static java.util.Collections.emptyList;


public class IndustrialCostDataBuilder extends BaseDataBuilder<IndustrialCostData, IndustrialCostDataBuilder> {

    public IndustrialCostDataBuilder withApplicationFinance(ApplicationFinanceResource applicationFinance) {
        return with(data -> data.setApplicationFinance(applicationFinance));
    }

    public IndustrialCostDataBuilder withCompetition(CompetitionResource competitionResource) {
        return with(data -> data.setCompetition(competitionResource));
    }

    public IndustrialCostDataBuilder withLabourEntry(String role, Integer annualSalary, Integer daysToBeSpent) {
        return with(data -> {

            QuestionResource question = retrieveQuestionByCompetitionAndName("Labour", data.getCompetition());

            LabourCost cost = newLabourCost().withId().
                    withName(null).
                    withRole(role).
                    withGrossAnnualSalary(bd(annualSalary)).
                    withLabourDays(daysToBeSpent).
                    withDescription(null).
                    build();

            financeRowService.addCost(data.getApplicationFinance().getId(), question.getId(), cost).
                    getSuccessObjectOrThrowException();
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

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private BigDecimal bd(Integer value) {
        return BigDecimal.valueOf(value);
    }

}
