package org.innovateuk.ifs.project.queries.viewmodel;

import java.util.List;

/**
 * View model backing the internal Finance Team members view of the Finance Check Queries page
 */
public class FinanceChecksQueriesViewModel {
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private String financeContactName;
    private String financeContactEmail;
    private String financeContactPhoneNumber;
    private String querySection;
    private Long projectId;
    private String projectName;
    List<FinanceChecksQueriesQueryViewModel> queries;
    private Long organisationId;
    private String baseUrl;

    public FinanceChecksQueriesViewModel(String organisationName,
                                         boolean leadPartnerOrganisation,
                                         String financeContactName,
                                         String financeContactEmail,
                                         String financeContactPhoneNumber,
                                         String querySection,
                                         Long projectId,
                                         String projectName,
                                         List<FinanceChecksQueriesQueryViewModel> queries,
                                         Long organisationId,
                                         String baseUrl) {
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.financeContactName = financeContactName;
        this.financeContactEmail = financeContactEmail;
        this.financeContactPhoneNumber = financeContactPhoneNumber;
        this.querySection = querySection;
        this.projectId = projectId;
        this.projectName = projectName;
        this.queries = queries;
        this.organisationId = organisationId;
        this.baseUrl = baseUrl;

    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public boolean isLeadPartnerOrganisation() {
        return leadPartnerOrganisation;
    }

    public void setLeadPartnerOrganisation(boolean leadPartnerOrganisation) {
        this.leadPartnerOrganisation = leadPartnerOrganisation;
    }
    public String getFinanceContactName() {
        return financeContactName;
    }

    public void setFinanceContactName(String financeContactName) {
        this.financeContactName = financeContactName;
    }

    public String getFinanceContactEmail() {
        return financeContactEmail;
    }

    public void setFinanceContactEmail(String financeContactEmail) {
        this.financeContactEmail = financeContactEmail;
    }

    public String getFinanceContactPhoneNumber() {
        return financeContactPhoneNumber;
    }

    public void setFinanceContactPhoneNumber(String financeContactPhoneNumber) {
        this.financeContactPhoneNumber = financeContactPhoneNumber;
    }

    public String getQuerySection() {
        return querySection;
    }

    public void setQuerySection(String querySection) {
        this.querySection = querySection;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<FinanceChecksQueriesQueryViewModel> getQueries() {
        return queries;
    }

    public void setQueries(List<FinanceChecksQueriesQueryViewModel> queries) {
        this.queries = queries;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
