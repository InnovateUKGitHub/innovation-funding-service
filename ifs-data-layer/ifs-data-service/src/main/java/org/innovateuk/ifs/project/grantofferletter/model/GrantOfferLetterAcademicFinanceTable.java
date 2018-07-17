package org.innovateuk.ifs.project.grantofferletter.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * Holder of values for the grant offer letter academic finance table, used by the pdf renderer
 */

public class GrantOfferLetterAcademicFinanceTable extends BaseGrantOfferLetterFinanceTable {

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
    private BigDecimal incurredStaffTotal;
    private BigDecimal incurredTravelSubsistenceTotal;
    private BigDecimal incurredEquipmentTotal;
    private BigDecimal incurredOtherCostsTotal;
    private BigDecimal allocatedInvestigatorsTotal;
    private BigDecimal allocatedEstateCostsTotal;
    private BigDecimal allocatedOtherCostsTotal;
    private BigDecimal indirectCostsTotal;
    private BigDecimal exceptionsStaffTotal;
    private BigDecimal exceptionsTravelSubsistenceTotal;
    private BigDecimal exceptionsEquipmentTotal;
    private BigDecimal exceptionsOtherCostsTotal;
    private List<String> organisations;

    public GrantOfferLetterAcademicFinanceTable(Map<String, BigDecimal> incurredStaff,
                                                Map<String, BigDecimal> incurredTravelSubsistence,
                                                Map<String, BigDecimal> incurredEquipment,
                                                Map<String, BigDecimal> incurredOtherCosts,
                                                Map<String, BigDecimal> allocatedInvestigators,
                                                Map<String, BigDecimal> allocatedEstateCosts,
                                                Map<String, BigDecimal> allocatedOtherCosts,
                                                Map<String, BigDecimal> indirectCosts,
                                                Map<String, BigDecimal> exceptionsStaff,
                                                Map<String, BigDecimal> exceptionsTravelSubsistence,
                                                Map<String, BigDecimal> exceptionsEquipment,
                                                Map<String, BigDecimal> exceptionsOtherCosts,
                                                BigDecimal incurredStaffTotal,
                                                BigDecimal incurredTravelSubsistenceTotal,
                                                BigDecimal incurredEquipmentTotal,
                                                BigDecimal incurredOtherCostsTotal,
                                                BigDecimal allocatedInvestigatorsTotal,
                                                BigDecimal allocatedEstateCostsTotal,
                                                BigDecimal allocatedOtherCostsTotal,
                                                BigDecimal indirectCostsTotal,
                                                BigDecimal exceptionsStaffTotal,
                                                BigDecimal exceptionsTravelSubsistenceTotal,
                                                BigDecimal exceptionsEquipmentTotal,
                                                BigDecimal exceptionsOtherCostsTotal,
                                                List<String> organisations) {
        this.incurredStaff = incurredStaff;
        this.incurredTravelSubsistence = incurredTravelSubsistence;
        this.incurredEquipment = incurredEquipment;
        this.incurredOtherCosts = incurredOtherCosts;
        this.allocatedInvestigators = allocatedInvestigators;
        this.allocatedEstateCosts = allocatedEstateCosts;
        this.allocatedOtherCosts = allocatedOtherCosts;
        this.indirectCosts = indirectCosts;
        this.exceptionsStaff = exceptionsStaff;
        this.exceptionsTravelSubsistence = exceptionsTravelSubsistence;
        this.exceptionsEquipment = exceptionsEquipment;
        this.exceptionsOtherCosts = exceptionsOtherCosts;
        this.incurredStaffTotal = incurredStaffTotal;
        this.incurredTravelSubsistenceTotal = incurredTravelSubsistenceTotal;
        this.incurredEquipmentTotal = incurredEquipmentTotal;
        this.incurredOtherCostsTotal = incurredOtherCostsTotal;
        this.allocatedInvestigatorsTotal = allocatedInvestigatorsTotal;
        this.allocatedEstateCostsTotal = allocatedEstateCostsTotal;
        this.allocatedOtherCostsTotal = allocatedOtherCostsTotal;
        this.indirectCostsTotal = indirectCostsTotal;
        this.exceptionsStaffTotal = exceptionsStaffTotal;
        this.exceptionsTravelSubsistenceTotal = exceptionsTravelSubsistenceTotal;
        this.exceptionsEquipmentTotal = exceptionsEquipmentTotal;
        this.exceptionsOtherCostsTotal = exceptionsOtherCostsTotal;
        this.organisations = organisations;

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
        return incurredStaffTotal;
    }

    public BigDecimal getIncurredTravelSubsistenceTotal() {
        return incurredTravelSubsistenceTotal;
    }

    public BigDecimal getIncurredEquipmentTotal() {
        return incurredEquipmentTotal;
    }

    public BigDecimal getIncurredOtherCostsTotal() {
        return incurredOtherCostsTotal;
    }

    public BigDecimal getAllocatedInvestigatorsTotal() {
        return allocatedInvestigatorsTotal;
    }

    public BigDecimal getAllocatedEstateCostsTotal() {
        return allocatedEstateCostsTotal;
    }

    public BigDecimal getAllocatedOtherCostsTotal() {
        return allocatedOtherCostsTotal;
    }

    public BigDecimal getIndirectCostsTotal() {
        return indirectCostsTotal;
    }

    public BigDecimal getExceptionsStaffTotal() {
        return exceptionsStaffTotal;
    }

    public BigDecimal getExceptionsTravelSubsistenceTotal() {
        return exceptionsTravelSubsistenceTotal;
    }

    public BigDecimal getExceptionsEquipmentTotal() {
        return exceptionsEquipmentTotal;
    }

    public BigDecimal getExceptionsOtherCostsTotal() {
        return exceptionsOtherCostsTotal;
    }

}
