package org.innovateuk.ifs.application.populator.finance.viewmodel;

import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Base viewmodel for the overview financesViewModel
 */
public abstract class BaseFinanceOverviewViewModel {
    protected BigDecimal financeTotal;
    protected Map<FinanceRowType, BigDecimal> financeTotalPerType;
    protected Map<Long, BaseFinanceResource> organisationFinances;
    protected BigDecimal totalFundingSought;
    protected BigDecimal totalContribution;
    protected BigDecimal totalOtherFunding;

    protected SectionResource financeSection;
    protected List<SectionResource> financeSectionChildren;
    protected Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap;
    protected Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs;

    public BigDecimal getFinanceTotal() {
        return financeTotal;
    }

    public void setFinanceTotal(BigDecimal financeTotal) {
        this.financeTotal = financeTotal;
    }

    public Map<FinanceRowType, BigDecimal> getFinanceTotalPerType() {
        return financeTotalPerType;
    }

    public void setFinanceTotalPerType(Map<FinanceRowType, BigDecimal> financeTotalPerType) {
        this.financeTotalPerType = financeTotalPerType;
    }

    public Map<Long, BaseFinanceResource> getOrganisationFinances() {
        return organisationFinances;
    }

    public void setOrganisationFinances(Map<Long, BaseFinanceResource> organisationFinances) {
        this.organisationFinances = organisationFinances;
    }

    public BigDecimal getTotalFundingSought() {
        return totalFundingSought;
    }

    public void setTotalFundingSought(BigDecimal totalFundingSought) {
        this.totalFundingSought = totalFundingSought;
    }

    public BigDecimal getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(BigDecimal totalContribution) {
        this.totalContribution = totalContribution;
    }

    public BigDecimal getTotalOtherFunding() {
        return totalOtherFunding;
    }

    public void setTotalOtherFunding(BigDecimal totalOtherFunding) {
        this.totalOtherFunding = totalOtherFunding;
    }

    public SectionResource getFinanceSection() {
        return financeSection;
    }

    public void setFinanceSection(SectionResource financeSection) {
        this.financeSection = financeSection;
    }

    public List<SectionResource> getFinanceSectionChildren() {
        return financeSectionChildren;
    }

    public void setFinanceSectionChildren(List<SectionResource> financeSectionChildren) {
        this.financeSectionChildren = financeSectionChildren;
    }

    public Map<Long, List<QuestionResource>> getFinanceSectionChildrenQuestionsMap() {
        return financeSectionChildrenQuestionsMap;
    }

    public void setFinanceSectionChildrenQuestionsMap(Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap) {
        this.financeSectionChildrenQuestionsMap = financeSectionChildrenQuestionsMap;
    }

    public Map<Long, List<FormInputResource>> getFinanceSectionChildrenQuestionFormInputs() {
        return financeSectionChildrenQuestionFormInputs;
    }

    public void setFinanceSectionChildrenQuestionFormInputs(Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs) {
        this.financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionFormInputs;
    }

    public Boolean getHasAcademicFileEntries() {
        return Boolean.FALSE;
    }

    public Boolean hasTooHighResearchRatio(Double maxRatio) {
        return Boolean.FALSE;
    }

    public Boolean getHasOrganisationFinances() {
        return null != organisationFinances;
    }

    public Boolean hasFinancesForOrganisation(Long organisationId) {
        return (organisationId != null) && getHasOrganisationFinances() && organisationFinances.containsKey(organisationId);
    }
}
