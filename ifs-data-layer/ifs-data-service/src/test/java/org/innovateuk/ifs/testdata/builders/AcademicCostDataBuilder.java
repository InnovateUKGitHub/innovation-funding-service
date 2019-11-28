package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.testdata.builders.data.AcademicCostData;
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
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Generates Academic Finances for an Organisation on an Application
 */
public class AcademicCostDataBuilder extends BaseDataBuilder<AcademicCostData, AcademicCostDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDataBuilder.class);

    public AcademicCostDataBuilder withApplicationFinance(ApplicationFinanceResource applicationFinance) {
        return with(data -> data.setApplicationFinance(applicationFinance));
    }

    public AcademicCostDataBuilder withCompetition(CompetitionResource competitionResource) {
        return with(data -> data.setCompetition(competitionResource));
    }

    public AcademicCostDataBuilder withTsbReference(String value) {
        return addCostItem("Provide the project costs for '{organisationName}'", (finance) -> new AcademicCost(null, "tsb_reference", null, value, FinanceRowType.YOUR_FINANCE, finance.getId()));
    }

    public AcademicCostDataBuilder withDirectlyIncurredStaff(BigDecimal value) {
        return addCostItem("Labour", (finance) -> new AcademicCost(null, "incurred_staff", value, null, FinanceRowType.LABOUR, finance.getId()));
    }

    public AcademicCostDataBuilder withDirectlyIncurredTravelAndSubsistence(BigDecimal value) {
        return addCostItem("Travel and subsistence", (finance) -> new AcademicCost(null, "incurred_travel_subsistence", value, null, FinanceRowType.TRAVEL, finance.getId()));
    }

    public AcademicCostDataBuilder withDirectlyIncurredOtherCosts(BigDecimal value) {
        return addCostItem("Materials", (finance) -> new AcademicCost(null, "incurred_other_costs", value, null, FinanceRowType.MATERIALS, finance.getId()));
    }

    public AcademicCostDataBuilder withDirectlyAllocatedInvestigators(BigDecimal value) {
        return addCostItem("Labour", (finance) -> new AcademicCost(null, "allocated_investigators", value, null, FinanceRowType.LABOUR, finance.getId()));
    }

    public AcademicCostDataBuilder withDirectlyAllocatedEstateCosts(BigDecimal value) {
        return addCostItem("Other costs", (finance) -> new AcademicCost(null, "allocated_estates_costs", value, null, FinanceRowType.OTHER_COSTS, finance.getId()));
    }

    public AcademicCostDataBuilder withDirectlyAllocatedOtherCosts(BigDecimal value) {
        return addCostItem("Other costs", (finance) -> new AcademicCost(null, "allocated_other_costs", value, null, FinanceRowType.OTHER_COSTS, finance.getId()));
    }

    public AcademicCostDataBuilder withIndirectCosts(BigDecimal value) {
        return addCostItem("Overheads", (finance) -> new AcademicCost(null, "indirect_costs", value, null, FinanceRowType.OVERHEADS, finance.getId()));
    }

    public AcademicCostDataBuilder withExceptionsStaff(BigDecimal value) {
        return addCostItem("Labour", (finance) -> new AcademicCost(null, "exceptions_staff", value, null, FinanceRowType.LABOUR, finance.getId()));
    }

    public AcademicCostDataBuilder withExceptionsOtherCosts(BigDecimal value) {
        return addCostItem("Other costs", (finance) -> new AcademicCost(null, "exceptions_other_costs", value, null, FinanceRowType.OTHER_COSTS, finance.getId()));
    }

    public AcademicCostDataBuilder withOtherFunding(String fundingSource, LocalDate dateSecured, BigDecimal fundingAmount) {
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

    public AcademicCostDataBuilder withGrantClaim(Integer grantClaim) {
        return updateCostItem(GrantClaimPercentage.class, FinanceRowType.FINANCE, existingCost -> {
            existingCost.setPercentage(grantClaim);
            financeRowCostsService.update(existingCost.getId(), existingCost);
        });
    }

    public AcademicCostDataBuilder withUploadedJesForm() {
        return with(data -> {
            FileEntry fileEntry = fileEntryRepository.save(
                    new FileEntry(null, "jes-form" + data.getApplicationFinance().getId() + ".pdf", "application/pdf", 7945));

            ApplicationFinance finance = applicationFinanceRepository.findById(data.getApplicationFinance().getId()).get();
            finance.setFinanceFileEntry(fileEntry);
            applicationFinanceRepository.save(finance);
        });
    }

    public AcademicCostDataBuilder withWorkPostcode(String workPostcode) {
        return with(data -> {

            ApplicationFinanceResource applicationFinance =
                    financeService.getApplicationFinanceById(data.getApplicationFinance().getId()).
                            getSuccess();

            applicationFinance.setWorkPostcode(workPostcode);

            financeService.updateApplicationFinance(applicationFinance.getId(), applicationFinance);
        });
    }


    private AcademicCostDataBuilder addCostItem(String financeRowName, Function<ApplicationFinanceResource, FinanceRowItem> cost) {
        return with(data -> {

            FinanceRowItem newCostItem = cost.apply(data.getApplicationFinance());

            financeRowCostsService.create(newCostItem.getTargetId(), newCostItem).
                    getSuccess();
        });
    }

    private <T extends FinanceRowItem> AcademicCostDataBuilder updateCostItem(Class<T> clazz, FinanceRowType financeRowType, Consumer<T> updateFn) {
        return updateCostItem(clazz, financeRowType, c -> true, updateFn);
    }

    private <T extends FinanceRowItem> AcademicCostDataBuilder updateCostItem(Class<T> clazz, FinanceRowType financeRowType, Predicate<T> filterFn, Consumer<T> updateFn) {
        return with(data -> {
            List<FinanceRowItem> existingItems = getCostItems(data.getApplicationFinance().getId(), financeRowType);
            simpleFilter(existingItems, item -> filterFn.test((T) item)).forEach(item -> updateFn.accept((T) item));
        });
    }

    public static AcademicCostDataBuilder newAcademicCostData(ServiceLocator serviceLocator) {
        return new AcademicCostDataBuilder(emptyList(), serviceLocator);
    }

    private AcademicCostDataBuilder(List<BiConsumer<Integer, AcademicCostData>> multiActions,
                                    ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected AcademicCostDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AcademicCostData>> actions) {
        return new AcademicCostDataBuilder(actions, serviceLocator);
    }

    @Override
    protected AcademicCostData createInitial() {
        return new AcademicCostData();
    }

    @Override
    protected void postProcess(int index, AcademicCostData instance) {
        super.postProcess(index, instance);
        OrganisationResource organisation = organisationService.findById(instance.getApplicationFinance().getOrganisation()).getSuccess();
        ApplicationResource application = applicationService.getApplicationById(instance.getApplicationFinance().getApplication()).getSuccess();
        LOG.info("Created Academic Costs for Application '{}', Organisation '{}'", application.getName(), organisation.getName());
    }
}
