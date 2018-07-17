package org.innovateuk.ifs.project.queries.viewmodel;

import java.util.Map;

public class FinanceChecksQueriesAddQueryViewModel {
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private String financeContactName;
    private String financeContactEmail;
    private String financeContactPhoneNumber;
    private String querySection;
    private Long projectId;
    private String projectName;
    private Map<Long, String> newAttachmentLinks;
    private int maxQueryWords;
    private int maxQueryCharacters;
    private int maxTitleCharacters;
    private Long organisationId;
    private String baseUrl;
    private Long applicationId;


    public FinanceChecksQueriesAddQueryViewModel(String organisationName,
                                         boolean leadPartnerOrganisation,
                                         String financeContactName,
                                         String financeContactEmail,
                                         String financeContactPhoneNumber,
                                         String querySection,
                                         Long projectId,
                                         String projectName,
                                         Map<Long, String> newAttachmentLinks,
                                         int maxQueryWords,
                                         int maxQueryCharacters,
                                         int maxTitleCharacters,
                                         Long organisationId,
                                         String baseUrl,
                                         Long applicationId) {
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.financeContactName = financeContactName;
        this.financeContactEmail = financeContactEmail;
        this.financeContactPhoneNumber = financeContactPhoneNumber;
        this.querySection = querySection;
        this.projectId = projectId;
        this.projectName = projectName;
        this.newAttachmentLinks = newAttachmentLinks;
        this.maxQueryWords = maxQueryWords;
        this.maxQueryCharacters = maxQueryCharacters;
        this.maxTitleCharacters = maxTitleCharacters;
        this.organisationId = organisationId;
        this.baseUrl = baseUrl;
        this.applicationId = applicationId;

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

    public Map<Long, String>  getNewAttachmentLinks() {
        return newAttachmentLinks;
    }

    public void setNewAttachmentLinks(Map<Long, String>  newAttachmentLinks) {
        this.newAttachmentLinks = newAttachmentLinks;
    }

    public int getMaxQueryWords() {
        return maxQueryWords;
    }

    public void setMaxQueryWords(int maxQueryWords) {
        this.maxQueryWords = maxQueryWords;
    }

    public int getMaxQueryCharacters() {
        return maxQueryCharacters;
    }

    public void setMaxQueryCharacters(int maxQueryCharacters) {
        this.maxQueryCharacters = maxQueryCharacters;
    }

    public int getMaxTitleCharacters() {
        return maxTitleCharacters;
    }

    public void setMaxTitleCharacters(int maxTitleCharacters) {
        this.maxTitleCharacters = maxTitleCharacters;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getOrganisationId() { return this.organisationId; }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
