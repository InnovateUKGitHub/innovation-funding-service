package com.worth.ifs.testdata;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.worth.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.Collections.emptyList;


public class IndustrialCostDataBuilder extends BaseDataBuilder<IndustrialCostData, IndustrialCostDataBuilder> {

    public IndustrialCostDataBuilder withApplicationFinance(ApplicationFinanceResource applicationFinance) {
        return with(data -> data.setApplicationFinance(applicationFinance));
    }

    public IndustrialCostDataBuilder withCompetition(CompetitionResource competitionResource) {
        return with(data -> data.setCompetition(competitionResource));
    }

    public IndustrialCostDataBuilder withWorkingDaysPerYear(Integer workingDays) {
        return updateCostItem(LabourCost.class, "Labour", item -> LabourCostCategory.WORKING_DAYS_PER_YEAR.equals(item.getRole()), existingCost -> {
            existingCost.setLabourDays(workingDays);
            financeRowService.updateCost(existingCost.getId(), existingCost);
        });
    }

    public IndustrialCostDataBuilder withOtherFunding(String fundingSource, LocalDate dateSecured, BigDecimal fundingAmount) {
        return updateCostItem(OtherFunding.class, "Other funding", existingCost -> {
            existingCost.setOtherPublicFunding("Yes");
            financeRowService.updateCost(existingCost.getId(), existingCost);
        }).addCostItem("Other funding", () -> {
            OtherFunding otherFunding = new OtherFunding();
            otherFunding.setFundingAmount(fundingAmount);
            otherFunding.setFundingSource(fundingSource);
            otherFunding.setSecuredDate(dateSecured.format(DateTimeFormatter.ofPattern("MM-yyyy")));
            return otherFunding;
        });
    }

    public IndustrialCostDataBuilder withGrantClaim(Integer grantClaim) {
        return updateCostItem(GrantClaim.class, "Funding level", existingCost -> {
            existingCost.setGrantClaimPercentage(grantClaim);
            financeRowService.updateCost(existingCost.getId(), existingCost);
        });
    }

    public IndustrialCostDataBuilder withLabourEntry(String role, Integer annualSalary, Integer daysToBeSpent) {
        return addCostItem("Labour", () ->
                newLabourCost().withId().
                    withName().
                    withRole(role).
                    withGrossAnnualSalary(bd(annualSalary)).
                    withLabourDays(daysToBeSpent).
                    withDescription().
                    build());
    }

    public IndustrialCostDataBuilder withAdministrationSupportCostsNone() {
        return doSetAdministrativeSupportCosts(OverheadRateType.NONE, OverheadRateType.NONE.getRate());
    }

    public IndustrialCostDataBuilder withAdministrationSupportCostsDefaultRate() {
        return doSetAdministrativeSupportCosts(OverheadRateType.DEFAULT_PERCENTAGE, OverheadRateType.DEFAULT_PERCENTAGE.getRate());
    }

    public IndustrialCostDataBuilder withAdministrationSupportCostsCustomRate(Integer customRate) {
        return doSetAdministrativeSupportCosts(OverheadRateType.CUSTOM_RATE, customRate);
    }

    private IndustrialCostDataBuilder doSetAdministrativeSupportCosts(OverheadRateType rateType, Integer rate) {
        return updateCostItem(Overhead.class, FinanceRowType.OVERHEADS.getName(), existingCost -> {
            Overhead updated = new Overhead(existingCost.getId(), rateType, rate);
            financeRowService.updateCost(existingCost.getId(), updated);
        });
    }

    private <T extends FinanceRowItem> IndustrialCostDataBuilder updateCostItem(Class<T> clazz, String financeRowName, Consumer<T> updateFn) {
        return updateCostItem(clazz, financeRowName, c -> true, updateFn);
    }

    private <T extends FinanceRowItem> IndustrialCostDataBuilder updateCostItem(Class<T> clazz, String financeRowName, Predicate<T> filterFn, Consumer<T> updateFn) {
        return with(data -> {

            QuestionResource question = retrieveQuestionByCompetitionAndName(financeRowName, data.getCompetition());

            List<FinanceRowItem> existingItems = financeRowService.getCostItems(data.getApplicationFinance().getId(), question.getId()).getSuccessObjectOrThrowException();
            simpleFilter(existingItems, item -> filterFn.test((T) item)).forEach(item -> updateFn.accept((T) item));
        });
    }

    private IndustrialCostDataBuilder addCostItem(String financeRowName, Supplier<FinanceRowItem> cost) {
        return with(data -> {

            FinanceRowItem newCostItem = cost.get();

            QuestionResource question = retrieveQuestionByCompetitionAndName(financeRowName, data.getCompetition());

            financeRowService.addCost(data.getApplicationFinance().getId(), question.getId(), newCostItem).
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
