package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.user.resource.OrganisationSize;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Base viewmodel for academic finances
 */
public class FinanceViewModel extends BaseFinanceViewModel {
    protected Map<FinanceRowType, FinanceRowCostCategory> organisationFinance;
    protected OrganisationSize organisationFinanceSize;
    protected OrganisationTypeResource organisationType;
    protected Long organisationFinanceId;
    protected BigDecimal organisationFinanceTotal;

    protected Integer organisationGrantClaimPercentage;
    protected Long organisationGrantClaimPercentageId;

    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinance() {
        return organisationFinance;
    }

    public void setOrganisationFinance(Map<FinanceRowType, FinanceRowCostCategory> organisationFinance) {
        this.organisationFinance = organisationFinance;
    }

    public OrganisationSize getOrganisationFinanceSize() {
        return organisationFinanceSize;
    }

    public void setOrganisationFinanceSize(OrganisationSize organisationFinanceSize) {
        this.organisationFinanceSize = organisationFinanceSize;
    }

    public OrganisationTypeResource getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationTypeResource organisationType) {
        this.organisationType = organisationType;
    }

    public Long getOrganisationFinanceId() {
        return organisationFinanceId;
    }

    public void setOrganisationFinanceId(Long organisationFinanceId) {
        this.organisationFinanceId = organisationFinanceId;
    }

    public BigDecimal getOrganisationFinanceTotal() {
        return organisationFinanceTotal;
    }

    public void setOrganisationFinanceTotal(BigDecimal organisationFinanceTotal) {
        this.organisationFinanceTotal = organisationFinanceTotal;
    }

    public Integer getOrganisationGrantClaimPercentage() {
        return organisationGrantClaimPercentage;
    }

    public void setOrganisationGrantClaimPercentage(Integer organisationGrantClaimPercentage) {
        this.organisationGrantClaimPercentage = organisationGrantClaimPercentage;
    }

    public Long getOrganisationGrantClaimPercentageId() {
        return organisationGrantClaimPercentageId;
    }

    public void setOrganisationGrantClaimPercentageId(Long organisationGrantClaimPercentageId) {
        this.organisationGrantClaimPercentageId = organisationGrantClaimPercentageId;
    }

    @Override
    public Boolean getHasOrganisationFinance(){
        return null != organisationFinance;
    }
}
