package org.innovateuk.ifs.project.grantofferletter.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterAcademicFinanceTable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;


/**
 * Builder for GrantOfferLetterAcademicFinanceTables.
 */

public class GrantOfferLetterAcademicFinanceTableBuilder extends BaseBuilder<GrantOfferLetterAcademicFinanceTable, GrantOfferLetterAcademicFinanceTableBuilder> {
    private GrantOfferLetterAcademicFinanceTableBuilder(List<BiConsumer<Integer, GrantOfferLetterAcademicFinanceTable>> multiActions) {
        super(multiActions);
    }

    public static GrantOfferLetterAcademicFinanceTableBuilder newGrantOfferLetterAcademicFinanceTable() {
        return new GrantOfferLetterAcademicFinanceTableBuilder(emptyList());
    }

    @Override
    protected GrantOfferLetterAcademicFinanceTableBuilder createNewBuilderWithActions(List<BiConsumer<Integer, GrantOfferLetterAcademicFinanceTable>> actions) {
        return new GrantOfferLetterAcademicFinanceTableBuilder(actions);
    }

    @Override
    protected GrantOfferLetterAcademicFinanceTable createInitial() {
        BigDecimal dummyCost = BigDecimal.ZERO;
        Map<String, BigDecimal> dummyCosts = singletonMap("Org", dummyCost);
        return new GrantOfferLetterAcademicFinanceTable(
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCosts,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                dummyCost,
                singletonList("Org")
        );
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredStaff(Map<String, BigDecimal>... incurredStaff) {
        return withArray((costs, academicFinanceTable) -> setField("incurredStaff", costs, academicFinanceTable), incurredStaff);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredTravelSubsistence(Map<String, BigDecimal>... incurredTravelSubsistence) {
        return withArray((costs, academicFinanceTable) -> setField("incurredTravelSubsistence", costs, academicFinanceTable), incurredTravelSubsistence);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredEquipment(Map<String, BigDecimal>... incurredEquipment) {
        return withArray((costs, academicFinanceTable) -> setField("incurredEquipment", costs, academicFinanceTable), incurredEquipment);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredOtherCosts(Map<String, BigDecimal>... incurredOtherCosts) {
        return withArray((costs, academicFinanceTable) -> setField("incurredOtherCosts", costs, academicFinanceTable), incurredOtherCosts);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withAllocatedInvestigators(Map<String, BigDecimal>... allocatedInvestigators) {
        return withArray((costs, academicFinanceTable) -> setField("allocatedInvestigators", costs, academicFinanceTable), allocatedInvestigators);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withAllocatedEstateCosts(Map<String, BigDecimal>... allocatedEstateCosts) {
        return withArray((costs, academicFinanceTable) -> setField("allocatedEstateCosts", costs, academicFinanceTable), allocatedEstateCosts);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withAllocatedOtherCosts(Map<String, BigDecimal>... allocatedOtherCosts) {
        return withArray((costs, academicFinanceTable) -> setField("allocatedOtherCosts", costs, academicFinanceTable), allocatedOtherCosts);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIndirectCosts(Map<String, BigDecimal>... indirectCosts) {
        return withArray((costs, academicFinanceTable) -> setField("indirectCosts", costs, academicFinanceTable), indirectCosts);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsStaff(Map<String, BigDecimal>... exceptionsStaff) {
        return withArray((costs, academicFinanceTable) -> setField("exceptionsStaff", costs, academicFinanceTable), exceptionsStaff);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsTravelSubsistence(Map<String, BigDecimal>... exceptionsTravelSubsistence) {
        return withArray((costs, academicFinanceTable) -> setField("exceptionsTravelSubsistence", costs, academicFinanceTable), exceptionsTravelSubsistence);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsEquipment(Map<String, BigDecimal>... exceptionsEquipment) {
        return withArray((costs, academicFinanceTable) -> setField("exceptionsEquipment", costs, academicFinanceTable), exceptionsEquipment);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsOtherCosts(Map<String, BigDecimal>... exceptionsOtherCosts) {
        return withArray((costs, academicFinanceTable) -> setField("exceptionsOtherCosts", costs, academicFinanceTable), exceptionsOtherCosts);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredStaffTotal(BigDecimal... incurredStaffTotal) {
        return withArray((total, academicFinanceTable) -> setField("incurredStaffTotal", total, academicFinanceTable), incurredStaffTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredTravelSubsistenceTotal(BigDecimal... incurredTravelSubsistenceTotal) {
        return withArray((total, academicFinanceTable) -> setField("incurredTravelSubsistenceTotal", total, academicFinanceTable), incurredTravelSubsistenceTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredEquipmentTotal(BigDecimal... incurredEquipmentTotal) {
        return withArray((total, academicFinanceTable) -> setField("incurredEquipmentTotal", total, academicFinanceTable), incurredEquipmentTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIncurredOtherCostsTotal(BigDecimal... incurredOtherCostsTotal) {
        return withArray((total, academicFinanceTable) -> setField("incurredOtherCostsTotal", total, academicFinanceTable), incurredOtherCostsTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withAllocatedInvestigatorsTotal(BigDecimal... allocatedInvestigatorsTotal) {
        return withArray((total, academicFinanceTable) -> setField("allocatedInvestigatorsTotal", total, academicFinanceTable), allocatedInvestigatorsTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withAllocatedEstateCostsTotal(BigDecimal... allocatedEstateCostsTotal) {
        return withArray((total, academicFinanceTable) -> setField("allocatedEstateCostsTotal", total, academicFinanceTable), allocatedEstateCostsTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withAllocatedOtherCostsTotal(BigDecimal... allocatedOtherCostsTotal) {
        return withArray((total, academicFinanceTable) -> setField("allocatedOtherCostsTotal", total, academicFinanceTable), allocatedOtherCostsTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withIndirectCostsTotal(BigDecimal... indirectCostsTotal) {
        return withArray((total, academicFinanceTable) -> setField("indirectCostsTotal", total, academicFinanceTable), indirectCostsTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsStaffTotal(BigDecimal... exceptionsStaffTotal) {
        return withArray((total, academicFinanceTable) -> setField("exceptionsStaffTotal", total, academicFinanceTable), exceptionsStaffTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsTravelSubsistenceTotal(BigDecimal... exceptionsTravelSubsistenceTotal) {
        return withArray((total, academicFinanceTable) -> setField("exceptionsTravelSubsistenceTotal", total, academicFinanceTable), exceptionsTravelSubsistenceTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsEquipmentTotal(BigDecimal... exceptionsEquipmentTotal) {
        return withArray((total, academicFinanceTable) -> setField("exceptionsEquipmentTotal", total, academicFinanceTable), exceptionsEquipmentTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withExceptionsOtherCostsTotal(BigDecimal... exceptionsOtherCostsTotal) {
        return withArray((total, academicFinanceTable) -> setField("exceptionsOtherCostsTotal", total, academicFinanceTable), exceptionsOtherCostsTotal);
    }

    public GrantOfferLetterAcademicFinanceTableBuilder withOrganisations(List<String>... organisations) {
        return withArray((orgs, academicFinanceTable) -> setField("organisations", orgs, academicFinanceTable), organisations);
    }

}
