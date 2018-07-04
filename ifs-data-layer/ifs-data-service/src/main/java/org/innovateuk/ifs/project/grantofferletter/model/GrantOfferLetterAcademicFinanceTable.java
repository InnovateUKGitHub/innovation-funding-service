package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator.*;

/**
 * Creates the grant offer letter academic finance table, used by the html renderer for the grant offer letter
 */
@Component
public class GrantOfferLetterAcademicFinanceTable extends GrantOfferLetterFinanceTable {

    private Map<String, BigDecimal> incurredStaff;
    private Map<String, BigDecimal> incurredTravelSubsistence;
    private Map<String, BigDecimal> incurredEquipment;
    private Map<String, BigDecimal> incurredOtherCosts;
    private Map<String, BigDecimal> allocatedInvestigators;
    private Map<String, BigDecimal> allocatedEstateCosts;
    private Map<String, BigDecimal> allocatedOtherCosts;
    private Map<String, BigDecimal> indirectCosts;
    private Map<String, BigDecimal> exceptionsStaff;
    private Map<String, BigDecimal> exceptionsTravelSubsistence;
    private Map<String, BigDecimal> exceptionsEquipment;
    private Map<String, BigDecimal> exceptionsOtherCosts;
    private List<String> organisations;

    public void populate(Map<String, List<ProjectFinanceRow>> financials) {
        incurredStaff = sumByFinancialType(financials, DIRECTLY_INCURRED_STAFF.getFinanceRowName());
        incurredTravelSubsistence = sumByFinancialType(financials, DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getFinanceRowName());
        incurredEquipment = sumByFinancialType(financials, DIRECTLY_INCURRED_EQUIPMENT.getFinanceRowName());
        incurredOtherCosts = sumByFinancialType(financials, DIRECTLY_INCURRED_OTHER_COSTS.getFinanceRowName());
        allocatedInvestigators = sumByFinancialType(financials, DIRECTLY_ALLOCATED_INVESTIGATORS.getFinanceRowName());
        allocatedEstateCosts = sumByFinancialType(financials, DIRECTLY_ALLOCATED_ESTATES_COSTS.getFinanceRowName());
        allocatedOtherCosts = sumByFinancialType(financials, DIRECTLY_INCURRED_OTHER_COSTS.getFinanceRowName());
        indirectCosts = sumByFinancialType(financials, INDIRECT_COSTS_OTHER_COSTS.getFinanceRowName());
        exceptionsStaff = sumByFinancialType(financials, INDIRECT_COSTS_STAFF.getFinanceRowName());
        exceptionsTravelSubsistence = sumByFinancialType(financials, INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getFinanceRowName());
        exceptionsEquipment = sumByFinancialType(financials, INDIRECT_COSTS_EQUIPMENT.getFinanceRowName());
        exceptionsOtherCosts = sumByFinancialType(financials, INDIRECT_COSTS_OTHER_COSTS.getFinanceRowName());
        organisations = new ArrayList<>(financials.keySet());
    }

    public List<String> getOrganisations() {
        return organisations;
    }
    public BigDecimal getIncurredStaff(String org) {
        return incurredStaff.get(org);
    }
    public BigDecimal getIncurredTravelSubsistence(String org) {
        return incurredTravelSubsistence.get(org);
    }
    public BigDecimal getIncurredEquipment(String org) {
        return incurredEquipment.get(org);
    }
    public BigDecimal getIncurredOtherCosts(String org) {
        return incurredOtherCosts.get(org);
    }
    public BigDecimal getAllocatedInvestigators(String org) {
        return allocatedInvestigators.get(org);
    }
    public BigDecimal getAllocatedEstateCosts(String org) {
        return allocatedEstateCosts.get(org);
    }
    public BigDecimal getAllocatedOtherCosts(String org) {
        return allocatedOtherCosts.get(org);
    }
    public BigDecimal getIndirectCosts(String org) {
        return indirectCosts.get(org);
    }
    public BigDecimal getExceptionsStaff(String org) {
        return exceptionsStaff.get(org);
    }
    public BigDecimal getExceptionsTravelSubsistence(String org) {
        return exceptionsTravelSubsistence.get(org);
    }
    public BigDecimal getExceptionsEquipment(String org) {
        return exceptionsEquipment.get(org);
    }
    public BigDecimal getExceptionsOtherCosts(String org) {
        return exceptionsOtherCosts.get(org);
    }

    public BigDecimal getIncurredStaffTotal() {
        return incurredStaff
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getIncurredTravelSubsistenceTotal() {
        return incurredTravelSubsistence
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getIncurredEquipmentTotal() {
        return incurredEquipment
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getIncurredOtherCostsTotal() {
        return incurredOtherCosts
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getAllocatedInvestigatorsTotal() {
        return allocatedInvestigators
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getAllocatedEstateCostsTotal() {
        return allocatedEstateCosts
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getAllocatedOtherCostsTotal() {
        return allocatedOtherCosts
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getIndirectCostsTotal() {
        return indirectCosts
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getExceptionsTravelSubsistenceTotal() {
        return incurredTravelSubsistence
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getExceptionsStaffTotal() {
        return exceptionsStaff
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getExceptionsEquipmentTotal() {
        return incurredEquipment
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getExceptionsOtherCostsTotal() {
        return exceptionsOtherCosts
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
