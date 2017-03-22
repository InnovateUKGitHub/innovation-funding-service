package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Base viewmodel for academic finances
 */
public class FinanceViewModel extends BaseFinanceViewModel {
    protected Map<FinanceRowType, FinanceRowCostCategory> organisationFinance;
    protected Map<FinanceRowType, QuestionResource> financeQuestions;
    protected Long organisationFinanceSize;
    protected OrganisationTypeResource organisationType;
    protected Long organisationFinanceId;
    protected BigDecimal organisationFinanceTotal;
    protected List<OrganisationSizeResource> organisationSizes;

    protected Integer organisationGrantClaimPercentage;
    protected Integer maximumGrantClaimPercentage;
    protected Long organisationGrantClaimPercentageId;

    public Map<FinanceRowType, QuestionResource> getFinanceQuestions() {
        return financeQuestions;
    }

    public void setFinanceQuestions(Map<FinanceRowType, QuestionResource> financeQuestions) {
        this.financeQuestions = financeQuestions;
    }

    public Map<FinanceRowType, FinanceRowCostCategory> getOrganisationFinance() {
        return organisationFinance;
    }

    public void setOrganisationFinance(Map<FinanceRowType, FinanceRowCostCategory> organisationFinance) {
        this.organisationFinance = organisationFinance;
    }

    public Long getOrganisationFinanceSize() {
        return organisationFinanceSize;
    }

    public void setOrganisationFinanceSize(Long organisationFinanceSize) {
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

    public String getFilename() {
        return null;
    }

    public List<OrganisationSizeResource> getOrganisationSizes() {
        return organisationSizes;
    }

    public void setOrganisationSizes(List<OrganisationSizeResource> organisationSizes) {
        this.organisationSizes = organisationSizes;
    }

    public Integer getMaximumGrantClaimPercentage() {
        return maximumGrantClaimPercentage;
    }

    public void setMaximumGrantClaimPercentage(Integer maximumGrantClaimPercentage) {
        this.maximumGrantClaimPercentage = maximumGrantClaimPercentage;
    }
}
