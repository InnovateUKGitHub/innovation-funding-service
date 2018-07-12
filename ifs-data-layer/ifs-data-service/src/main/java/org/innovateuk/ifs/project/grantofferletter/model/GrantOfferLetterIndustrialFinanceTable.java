package org.innovateuk.ifs.project.grantofferletter.model;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Holder of values for the grant offer letter industrial finance table, used by the pdf renderer
 */

@Component
public class GrantOfferLetterIndustrialFinanceTable extends GrantOfferLetterFinanceTable {

    private Map<String, BigDecimal> labour;
    private Map<String, BigDecimal> materials;
    private Map<String, BigDecimal> overheads;
    private Map<String, BigDecimal> capitalUsage;
    private Map<String, BigDecimal> subcontract;
    private Map<String, BigDecimal> travel;
    private Map<String, BigDecimal> otherCosts;
    private BigDecimal labourTotal;
    private BigDecimal materialsTotal;
    private BigDecimal overheadsTotal;
    private BigDecimal capitalUsageTotal;
    private BigDecimal subcontractTotal;
    private BigDecimal travelTotal;
    private BigDecimal otherCostsTotal;
    private List<String> organisations;

    public GrantOfferLetterIndustrialFinanceTable() {

    }

    public GrantOfferLetterIndustrialFinanceTable(Map<String, BigDecimal> labour,
                                                  Map<String, BigDecimal> materials,
                                                  Map<String, BigDecimal> overheads,
                                                  Map<String, BigDecimal> capitalUsage,
                                                  Map<String, BigDecimal> subcontract,
                                                  Map<String, BigDecimal> travel,
                                                  Map<String, BigDecimal> otherCosts,
                                                  BigDecimal labourTotal,
                                                  BigDecimal materialsTotal,
                                                  BigDecimal overheadsTotal,
                                                  BigDecimal capitalUsageTotal,
                                                  BigDecimal subcontractTotal,
                                                  BigDecimal travelTotal,
                                                  BigDecimal otherCostsTotal,
                                                  List<String> organisations) {
        this.labour = labour;
        this.materials = materials;
        this.overheads = overheads;
        this.capitalUsage = capitalUsage;
        this.subcontract = subcontract;
        this.travel = travel;
        this.otherCosts = otherCosts;
        this.labourTotal = labourTotal;
        this.materialsTotal = materialsTotal;
        this.overheadsTotal = overheadsTotal;
        this.capitalUsageTotal = capitalUsageTotal;
        this.subcontractTotal = subcontractTotal;
        this.travelTotal = travelTotal;
        this.otherCostsTotal = otherCostsTotal;
        this.organisations = organisations;
    }

    public List<String> getOrganisations() {
        return organisations;
    }

    public BigDecimal getLabour(String organisation) {
        return labour.get(organisation);
    }
    public BigDecimal getMaterials(String organisation) {
        return materials.get(organisation);
    }
    public BigDecimal getOverheads(String organisation) {
        return overheads.get(organisation);
    }
    public BigDecimal getCapitalUsage(String organisation) {
        return capitalUsage.get(organisation);
    }
    public BigDecimal getSubcontract(String organisation) {
        return subcontract.get(organisation);
    }
    public BigDecimal getTravel(String organisation) {
        return travel.get(organisation);
    }

    public BigDecimal getOtherCosts(String organisation) {
        return otherCosts.get(organisation);
    }

    public BigDecimal getLabourTotal() {
        return labourTotal;
    }

    public BigDecimal getMaterialsTotal() {
        return materialsTotal;

    }

    public BigDecimal getOverheadsTotal() {
        return overheadsTotal;
    }

    public BigDecimal getCapitalUsageTotal() {
        return capitalUsageTotal;
    }

    public BigDecimal getSubcontractTotal() {
        return subcontractTotal;
    }

    public BigDecimal getTravelTotal() {
        return travelTotal;
    }

    public BigDecimal getOtherCostsTotal() {
        return otherCostsTotal;
    }
}
