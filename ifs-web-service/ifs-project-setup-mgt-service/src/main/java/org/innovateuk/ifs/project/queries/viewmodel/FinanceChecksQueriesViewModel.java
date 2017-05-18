package org.innovateuk.ifs.project.queries.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * View model backing the internal Finance Team members view of the Finance Check Queries page
 */
public class FinanceChecksQueriesViewModel {
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private Optional<ProjectUserResource> financeContact;
    private String querySection;
    private Long projectId;
    private String projectName;
    List<ThreadViewModel> queries;
    private Long organisationId;
    private String baseUrl;
    private Map<Long, String> newAttachmentLinks;
    private int maxQueryWords;
    private int maxQueryCharacters;
    private Long queryId;

    public FinanceChecksQueriesViewModel(String organisationName,
                                         boolean leadPartnerOrganisation,
                                         Optional<ProjectUserResource> financeContact,
                                         String querySection,
                                         Long projectId,
                                         String projectName,
                                         List<ThreadViewModel> queries,
                                         Long organisationId,
                                         String baseUrl,
                                         Map<Long, String> newAttachmentLinks,
                                         int maxQueryWords,
                                         int maxQueryCharacters,
                                         Long queryId) {
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.financeContact= financeContact;
        this.querySection = querySection;
        this.projectId = projectId;
        this.projectName = projectName;
        this.queries = queries;
        this.organisationId = organisationId;
        this.baseUrl = baseUrl;
        this.newAttachmentLinks = newAttachmentLinks;
        this.maxQueryWords = maxQueryWords;
        this.maxQueryCharacters = maxQueryCharacters;
        this.queryId = queryId;
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
    public Optional<ProjectUserResource> getFinanceContact() {
        return financeContact;
    }

    public void setFinanceContact(Optional<ProjectUserResource> financeContact) {
        this.financeContact = financeContact;
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

    public List<ThreadViewModel> getQueries() {
        return queries;
    }

    public void setQueries(List<ThreadViewModel> queries) {
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

    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    public Map<Long, String> getNewAttachmentLinks() {

        return newAttachmentLinks;
    }

    public void setNewAttachmentLinks(Map<Long, String> newAttachmentLinks) {
        this.newAttachmentLinks = newAttachmentLinks;
    }
}
