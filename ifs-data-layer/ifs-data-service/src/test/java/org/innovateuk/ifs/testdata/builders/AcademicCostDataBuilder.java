package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.testdata.builders.data.AcademicCostData;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;

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
        return addCostItem("Provide the project costs for '{organisationName}'", () -> new AcademicCost(null, "tsb_reference", null, value));
    }

    public AcademicCostDataBuilder withDirectlyIncurredStaff(BigDecimal value) {
        return addCostItem("Labour", () -> new AcademicCost(null, "incurred_staff", value, null));
    }

    public AcademicCostDataBuilder withDirectlyIncurredTravelAndSubsistence(BigDecimal value) {
        return addCostItem("Travel and subsistence", () -> new AcademicCost(null, "incurred_travel_subsistence", value, null));
    }

    public AcademicCostDataBuilder withDirectlyIncurredOtherCosts(BigDecimal value) {
        return addCostItem("Materials", () -> new AcademicCost(null, "incurred_other_costs", value, null));
    }

    public AcademicCostDataBuilder withDirectlyAllocatedInvestigators(BigDecimal value) {
        return addCostItem("Labour", () -> new AcademicCost(null, "allocated_investigators", value, null));
    }

    public AcademicCostDataBuilder withDirectlyAllocatedEstateCosts(BigDecimal value) {
        return addCostItem("Other costs", () -> new AcademicCost(null, "allocated_estates_costs", value, null));
    }

    public AcademicCostDataBuilder withDirectlyAllocatedOtherCosts(BigDecimal value) {
        return addCostItem("Other costs", () -> new AcademicCost(null, "allocated_other_costs", value, null));
    }

    public AcademicCostDataBuilder withIndirectCosts(BigDecimal value) {
        return addCostItem("Overheads", () -> new AcademicCost(null, "indirect_costs", value, null));
    }

    public AcademicCostDataBuilder withExceptionsStaff(BigDecimal value) {
        return addCostItem("Labour", () -> new AcademicCost(null, "exceptions_staff", value, null));
    }

    public AcademicCostDataBuilder withExceptionsOtherCosts(BigDecimal value) {
        return addCostItem("Other costs", () -> new AcademicCost(null, "exceptions_other_costs", value, null));
    }

    public AcademicCostDataBuilder withUploadedJesForm() {
        return with(data -> {
            FileEntry fileEntry = fileEntryRepository.save(
                    new FileEntry(null, "jes-form" + data.getApplicationFinance().getId() + ".pdf", "application/pdf", 7945));

            ApplicationFinance finance = applicationFinanceRepository.findOne(data.getApplicationFinance().getId());
            finance.setFinanceFileEntry(fileEntry);
            applicationFinanceRepository.save(finance);
        });
    }

    private AcademicCostDataBuilder addCostItem(String financeRowName, Supplier<FinanceRowItem> cost) {
        return with(data -> {

            FinanceRowItem newCostItem = cost.get();

            QuestionResource question = retrieveQuestionByCompetitionAndName(financeRowName, data.getCompetition().getId());

            financeRowService.addCost(data.getApplicationFinance().getId(), question.getId(), newCostItem).
                    getSuccessObjectOrThrowException();
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
        OrganisationResource organisation = organisationService.findById(instance.getApplicationFinance().getOrganisation()).getSuccessObjectOrThrowException();
        ApplicationResource application = applicationService.getApplicationById(instance.getApplicationFinance().getApplication()).getSuccessObjectOrThrowException();
        LOG.info("Created Academic Costs for Application '{}', Organisation '{}'", application.getName(), organisation.getName());
    }
}
