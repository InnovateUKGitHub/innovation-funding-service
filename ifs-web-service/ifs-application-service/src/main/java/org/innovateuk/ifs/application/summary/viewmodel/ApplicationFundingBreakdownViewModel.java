package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicationFundingBreakdownViewModel {

    private final Map<FinanceRowType, BigDecimal> financeTotalPerType;
    private final List<OrganisationResource> applicationOrganisations;
    private final SectionResource financeSection;
    private BigDecimal financeTotal;
    private Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap;
    private List<SectionResource> financeSectionChildren;
    private Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs;
    private final OrganisationResource leadOrganisation;
    private final Map<Long, BaseFinanceResource> organisationFinances;
    private final List<String> pendingOrganisationNames;

    public ApplicationFundingBreakdownViewModel(
                                              Map<FinanceRowType, BigDecimal> financeTotalPerType,
                                              BigDecimal financeTotal,
                                              List<OrganisationResource> applicationOrganisations,
                                              SectionResource financeSection,
                                              List<SectionResource> financeSectionChildren,
                                              Map<Long, List<QuestionResource>> financeSectionChildrenQuestionsMap,
                                              Map<Long, List<FormInputResource>> financeSectionChildrenQuestionFormInputs,
                                              OrganisationResource leadOrganisation,
                                              Map<Long, BaseFinanceResource> organisationFinances,
                                              List<String> pendingOrganisationNames
    ) {
        this.financeTotalPerType = financeTotalPerType;
        this.financeTotal = financeTotal;
        this.applicationOrganisations = applicationOrganisations;
        this.financeSection = financeSection;
        this.financeSectionChildren = financeSectionChildren;
        this.financeSectionChildrenQuestionsMap = financeSectionChildrenQuestionsMap;
        this.financeSectionChildrenQuestionFormInputs = financeSectionChildrenQuestionFormInputs;
        this.leadOrganisation = leadOrganisation;
        this.organisationFinances = organisationFinances;
        this.pendingOrganisationNames = pendingOrganisationNames;
    }

    //For EOI Competitions
    public ApplicationFundingBreakdownViewModel(Map<FinanceRowType, BigDecimal> financeTotalPerType,
                                                List<OrganisationResource> applicationOrganisations,
                                                SectionResource financeSection,
                                                OrganisationResource leadOrganisation,
                                                Map<Long, BaseFinanceResource> organisationFinances,
                                                List<String> pendingOrganisationNames) {
        this.financeTotalPerType = financeTotalPerType;
        this.applicationOrganisations = applicationOrganisations;
        this.financeSection = financeSection;
        this.leadOrganisation = leadOrganisation;
        this.organisationFinances = organisationFinances;
        this.pendingOrganisationNames = pendingOrganisationNames;
    }

    public Map<FinanceRowType, BigDecimal> getFinanceTotalPerType() {
        return financeTotalPerType;
    }

    public BigDecimal getFinanceTotal() {
        return financeTotal;
    }

    public List<OrganisationResource> getApplicationOrganisations() {
        return applicationOrganisations;
    }

    public SectionResource getFinanceSection() {
        return financeSection;
    }

    public List<SectionResource> getFinanceSectionChildren() {
        return financeSectionChildren;
    }

    public Map<Long, List<QuestionResource>> getFinanceSectionChildrenQuestionsMap() {
        return financeSectionChildrenQuestionsMap;
    }

    public Map<Long, List<FormInputResource>> getFinanceSectionChildrenQuestionFormInputs() {
        return financeSectionChildrenQuestionFormInputs;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public List<String> getPendingOrganisationNames() {
        return pendingOrganisationNames;
    }

    public Map<Long, BaseFinanceResource> getOrganisationFinances() {
        return organisationFinances;
    }
}
