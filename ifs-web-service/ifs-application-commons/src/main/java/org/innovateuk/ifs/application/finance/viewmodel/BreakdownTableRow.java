package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class BreakdownTableRow {

    private final Long organisationId;
    private final String organisationName;
    private final String status;
    private final boolean showViewFinancesLink;
    private final String url;

    private final Map<FinanceRowType, BigDecimal> costs;
    private final BigDecimal total;

    public BreakdownTableRow(Long organisationId, String organisationName, String status, boolean showViewFinancesLink, String url, Map<FinanceRowType, BigDecimal> costs, BigDecimal total) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.status = status;
        this.showViewFinancesLink = showViewFinancesLink;
        this.url = url;
        this.costs = costs;
        this.total = total;
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

    public Map<FinanceRowType, BigDecimal> getCosts() {
        return costs;
    }

    /* view logic. */
    public BigDecimal getCost(FinanceRowType type) {
        return costs.get(type);
    }


    public static BreakdownTableRow pendingOrganisation(String name, List<FinanceRowType> types) {
        return new BreakdownTableRow(
                null,
                name,
                "(pending)",
                false,
                null,
                types.stream().collect(toMap(Function.identity(), (i) -> BigDecimal.ZERO)),
                BigDecimal.ZERO
        );
    }
}
