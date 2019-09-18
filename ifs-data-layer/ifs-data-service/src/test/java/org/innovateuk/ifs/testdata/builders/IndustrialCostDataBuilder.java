package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.testdata.builders.data.IndustrialCostData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Generates Indisutrial Finances for an Organisation on an Application
 */
public class IndustrialCostDataBuilder extends BaseDataBuilder<IndustrialCostData, IndustrialCostDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(IndustrialCostDataBuilder.class);

    public IndustrialCostDataBuilder withApplicationFinance(ApplicationFinanceResource applicationFinance) {
        return with(data -> data.setApplicationFinance(applicationFinance));
    }

    public IndustrialCostDataBuilder withCompetition(CompetitionResource competitionResource) {
        return with(data -> data.setCompetition(competitionResource));
    }

    public IndustrialCostDataBuilder withWorkingDaysPerYear(Integer workingDays) {
        return updateCostItem(LabourCost.class, FinanceRowType.LABOUR, item -> LabourCostCategory.WORKING_DAYS_PER_YEAR.equals(item.getRole()), existingCost -> {
            existingCost.setLabourDays(workingDays);
            financeRowCostsService.update(existingCost.getId(), existingCost);
        });
    }

    public IndustrialCostDataBuilder withOtherFunding(String fundingSource, LocalDate dateSecured, BigDecimal fundingAmount) {
        return updateCostItem(OtherFunding.class, FinanceRowType.OTHER_FUNDING, row -> OtherFundingCostCategory.OTHER_FUNDING.equals(row.getFundingSource()), existingCost -> {
            existingCost.setOtherPublicFunding("Yes");
            financeRowCostsService.update(existingCost.getId(), existingCost);
        }).addCostItem("Other funding", (finance) -> {
            OtherFunding otherFunding = new OtherFunding(finance.getId());
            otherFunding.setFundingAmount(fundingAmount);
            otherFunding.setFundingSource(fundingSource);
            otherFunding.setSecuredDate(dateSecured.format(DateTimeFormatter.ofPattern("MM-yyyy")));
            return otherFunding;
        });
    }

    public IndustrialCostDataBuilder withGrantClaim(Integer grantClaim) {
        return updateCostItem(GrantClaimPercentage.class, FinanceRowType.FINANCE, existingCost -> {
            existingCost.setPercentage(grantClaim);
            financeRowCostsService.update(existingCost.getId(), existingCost);
        });
    }

    public IndustrialCostDataBuilder withLabourEntry(String role, Integer annualSalary, Integer daysToBeSpent) {
        return addCostItem("Labour", (finance) ->
                newLabourCost().withId().
                    withName().
                    withRole(role).
                        withGrossEmployeeCost(bd(annualSalary)).
                    withLabourDays(daysToBeSpent).
                    withDescription().
                    withTargetId(finance.getId()).
                    build());
    }

    public IndustrialCostDataBuilder withMaterials(String item, BigDecimal cost, Integer quantity) {
        return addCostItem("Materials", (finance) ->
                newMaterials().
                        withId().
                        withItem(item).
                        withCost(cost).
                        withQuantity(quantity).
                        withTargetId(finance.getId()).
                        build());
    }

    public IndustrialCostDataBuilder withCapitalUsage(Integer depreciation, String description, boolean existing,
                                                      BigDecimal presentValue, BigDecimal residualValue,
                                                      Integer utilisation) {
        return addCostItem("Capital Usage", (finance) ->
                new CapitalUsage(null, depreciation, description, existing ? "Existing" : "New", presentValue, residualValue, utilisation, finance.getId()));
    }

    public IndustrialCostDataBuilder withSubcontractingCost(String name, String country, String role, BigDecimal cost) {
        return addCostItem("Sub-contracting costs", (finance) ->
                new SubContractingCost(null, cost, country, name, role, finance.getId()));
    }

    public IndustrialCostDataBuilder withTravelAndSubsistence(String purpose, Integer numberOfTimes, BigDecimal costEach) {
        return addCostItem("Travel and subsistence", (finance) ->
                new TravelCost(null, purpose, costEach, numberOfTimes, finance.getId()));
    }

    public IndustrialCostDataBuilder withOtherCosts(String description, BigDecimal estimatedCost) {
        return addCostItem("Other costs", (finance) ->
                new OtherCost(null, description, estimatedCost, finance.getId()));
    }

    public IndustrialCostDataBuilder withOrganisationSize(OrganisationSize organsationSize) {
        return with(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeService.getApplicationFinanceById(data.getApplicationFinance().getId()).
                            getSuccess();

            applicationFinance.setOrganisationSize(organsationSize);

            financeService.updateApplicationFinance(applicationFinance.getId(), applicationFinance);
        });
    }

    public IndustrialCostDataBuilder withWorkPostcode(String workPostcode) {
        return with(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeService.getApplicationFinanceById(data.getApplicationFinance().getId()).
                            getSuccess();

            applicationFinance.setWorkPostcode(workPostcode);

            financeService.updateApplicationFinance(applicationFinance.getId(), applicationFinance);
        });
    }

    public IndustrialCostDataBuilder withAdministrationSupportCostsNone() {
        return doSetAdministrativeSupportCosts(OverheadRateType.NONE, OverheadRateType.NONE.getRate());
    }

    public IndustrialCostDataBuilder withAdministrationSupportCostsDefaultRate() {
        return doSetAdministrativeSupportCosts(OverheadRateType.DEFAULT_PERCENTAGE, OverheadRateType.DEFAULT_PERCENTAGE.getRate());
    }

    public IndustrialCostDataBuilder withAdministrationSupportCostsTotalRate(Integer customRate) {
        return doSetAdministrativeSupportCosts(OverheadRateType.TOTAL, customRate);
    }

    public IndustrialCostDataBuilder withProcurementOverheads(String item, int project, int company) {
        return addCostItem("Procurement overhead", (finance) ->
                new ProcurementOverhead(null, item, BigDecimal.valueOf(project), company, finance.getId()));
    }

    public IndustrialCostDataBuilder withGrantClaimAmount(int amount) {
        return updateCostItem(GrantClaimAmount.class, FinanceRowType.GRANT_CLAIM_AMOUNT, existingCost -> {
            existingCost.setAmount(BigDecimal.valueOf(amount));
            financeRowCostsService.update(existingCost.getId(), existingCost);
        });
    }

    public IndustrialCostDataBuilder withVat(boolean registered) {
        return updateCostItem(Vat.class, FinanceRowType.VAT, existingCost -> {
            existingCost.setRegistered(registered);
            financeRowCostsService.update(existingCost.getId(), existingCost);
        });
    }
    private IndustrialCostDataBuilder doSetAdministrativeSupportCosts(OverheadRateType rateType, Integer rate) {
        return updateCostItem(Overhead.class, FinanceRowType.OVERHEADS, existingCost -> {
            Overhead updated = new Overhead(existingCost.getId(), rateType, rate, existingCost.getTargetId());
            financeRowCostsService.update(existingCost.getId(), updated);
        });
    }

    private <T extends FinanceRowItem> IndustrialCostDataBuilder updateCostItem(Class<T> clazz, FinanceRowType financeRowType, Consumer<T> updateFn) {
        return updateCostItem(clazz, financeRowType, c -> true, updateFn);
    }

    private <T extends FinanceRowItem> IndustrialCostDataBuilder updateCostItem(Class<T> clazz, FinanceRowType financeRowType, Predicate<T> filterFn, Consumer<T> updateFn) {
        return with(data -> {
            List<FinanceRowItem> existingItems = financeRowCostsService.getCostItems(data.getApplicationFinance().getId(), financeRowType).getSuccess();
            simpleFilter(existingItems, item -> filterFn.test((T) item)).forEach(item -> updateFn.accept((T) item));
        });
    }

    private IndustrialCostDataBuilder addCostItem(String financeRowName, Function<ApplicationFinanceResource, FinanceRowItem> cost) {
        return with(data -> {

            FinanceRowItem newCostItem = cost.apply(data.getApplicationFinance());

            financeRowCostsService.create(newCostItem.getTargetId(), newCostItem).
                    getSuccess();
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
    @Override
    protected void postProcess(int index, IndustrialCostData instance) {
        super.postProcess(index, instance);
        OrganisationResource organisation = organisationService.findById(instance.getApplicationFinance().getOrganisation()).getSuccess();
        ApplicationResource application = applicationService.getApplicationById(instance.getApplicationFinance().getApplication()).getSuccess();
        LOG.info("Created Industrial Costs for Application '{}', Organisation '{}'", application.getName(), organisation.getName());
    }

}
