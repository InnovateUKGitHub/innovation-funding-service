package org.innovateuk.ifs.project.queries.viewmodel;

import java.util.List;
import java.util.Map;

/**
 * View model backing the internal Finance Team members view of the Finance Check Queries page
 */
public class FinanceChecksQueriesViewModel {
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private String financeContactName;
    private String financeContactEmail;
    private String financeContactPhoneNumber;
    private boolean showNewQuery;
    private String querySection;
    private Long projectId;
    private String projectName;
    private Map<Long, String> newAttachmentLinks;
    private int maxQueryWords;
    private int maxQueryCharacters;
    private int maxTitleCharacters;
    List<FinanceChecksQueriesQueryViewModel> queries;
    private boolean showNewPost;
    private Long newPostQueryId;
    private Long organisationId;

    public FinanceChecksQueriesViewModel(String organisationName,
                                         boolean leadPartnerOrganisation,
                                         String financeContactName,
                                         String financeContactEmail,
                                         String financeContactPhoneNumber,
                                         boolean showNewQuery,
                                         String querySection,
                                         Long projectId,
                                         String projectName,
                                         Map<Long, String> newAttachmentLinks,
                                         int maxQueryWords,
                                         int maxQueryCharacters,
                                         int maxTitleCharacters,
                                         List<FinanceChecksQueriesQueryViewModel> queries,
                                         boolean showNewPost,
                                         Long newPostQueryId,
                                         Long organisationId) {
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.financeContactName = financeContactName;
        this.financeContactEmail = financeContactEmail;
        this.financeContactPhoneNumber = financeContactPhoneNumber;
        this.showNewQuery = showNewQuery;
        this.querySection = querySection;
        this.projectId = projectId;
        this.projectName = projectName;
        this.newAttachmentLinks = newAttachmentLinks;
        this.maxQueryWords = maxQueryWords;
        this.maxQueryCharacters = maxQueryCharacters;
        this.maxTitleCharacters = maxTitleCharacters;
        this.queries = queries;
        this.showNewPost = showNewPost;
        this.newPostQueryId = newPostQueryId;
        this.organisationId = organisationId;

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

    public boolean isShowNewQuery() {
        return showNewQuery;
    }

    public void setShowNewQuery(boolean showNewQuery) {
        this.showNewQuery = showNewQuery;
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

    public List<FinanceChecksQueriesQueryViewModel> getQueries() {
        return queries;
    }

    public void setQueries(List<FinanceChecksQueriesQueryViewModel> queries) {
        this.queries = queries;
    }

    public boolean isShowNewPost() {
        return showNewPost;
    }

    public void setShowNewPost(boolean showNewPost) {
        this.showNewPost = showNewPost;
    }
    public Long getNewPostQueryId() {
        return newPostQueryId;
    }

    public void setNewPostQueryId(Long newPostQueryId) {
        this.newPostQueryId = newPostQueryId;
    }
    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }
}
