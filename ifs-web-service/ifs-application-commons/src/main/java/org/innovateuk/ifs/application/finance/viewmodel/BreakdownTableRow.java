package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigDecimal;

public class BreakdownTableRow {

    private final Long organisationId;
    private final String organisationName;
    private final String status;
    private final boolean showViewFinancesLink;
    private final String url;

    private final BigDecimal total;
    private final BigDecimal labour;
    private final BigDecimal overheads;
    private final BigDecimal procurementOverheads;
    private final BigDecimal materials;
    private final BigDecimal capitalUsage;
    private final BigDecimal subcontracting;
    private final BigDecimal travel;
    private final BigDecimal other;

    public BreakdownTableRow(Long organisationId, String organisationName, String status, boolean showViewFinancesLink, String url, BigDecimal total, BigDecimal labour, BigDecimal overheads, BigDecimal procurementOverheads, BigDecimal materials, BigDecimal capitalUsage, BigDecimal subcontracting, BigDecimal travel, BigDecimal other) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.status = status;
        this.showViewFinancesLink = showViewFinancesLink;
        this.url = url;
        this.total = total;
        this.labour = labour;
        this.overheads = overheads;
        this.procurementOverheads = procurementOverheads;
        this.materials = materials;
        this.capitalUsage = capitalUsage;
        this.subcontracting = subcontracting;
        this.travel = travel;
        this.other = other;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getStatus() {
        return status;
    }

    public boolean isShowViewFinancesLink() {
        return showViewFinancesLink;
    }

    public String getUrl() {
        return url;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getLabour() {
        return labour;
    }

    public BigDecimal getOverheads() {
        return overheads;
    }

    public BigDecimal getProcurementOverheads() {
        return procurementOverheads;
    }

    public BigDecimal getMaterials() {
        return materials;
    }

    public BigDecimal getCapitalUsage() {
        return capitalUsage;
    }

    public BigDecimal getSubcontracting() {
        return subcontracting;
    }

    public BigDecimal getTravel() {
        return travel;
    }

    public BigDecimal getOther() {
        return other;
    }

    public static BreakdownTableRow pendingOrganisation(String name) {
        return new BreakdownTableRow(
                null,
                name,
                "(pending)",
                false,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }
}
