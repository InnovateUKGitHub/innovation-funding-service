package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryConstraints;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Map;

/**
 * View model to back the Finance Checks page.
 */
public class ProjectFinanceChecksViewModel {
    private Long projectId;
    private Long organisationId;
    private String projectName;
    private List<ThreadViewModel> queries;
    private boolean approved;
    private Map<Long, String> newAttachmentLinks;
    private int maxQueryWords;
    private int maxQueryCharacters;
    private Long queryId;
    private String baseUrl;
    private boolean isAcademic;

    public ProjectFinanceChecksViewModel(ProjectResource project, OrganisationResource organisation,
                                         List<ThreadViewModel> queries, boolean approved,
                                         Map<Long, String> newAttachmentLinks,
                                         int maxQueryWords,
                                         int maxQueryCharacters,
                                         Long queryId,
                                         String baseUrl,
                                         boolean isAcademic) {
        this.projectId = project.getId();
        this.organisationId = organisation.getId();
        this.projectName = project.getName();
        this.queries = queries;
        this.approved = approved;
        this.newAttachmentLinks = newAttachmentLinks;
        this.maxQueryWords = maxQueryWords;
        this.maxQueryCharacters = maxQueryCharacters;
        this.queryId = queryId;
        this.baseUrl = baseUrl;
        this.isAcademic = isAcademic;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }


    public Map<Long, String> getNewAttachmentLinks() {
        return newAttachmentLinks;
    }

    public void setNewAttachmentLinks(Map<Long, String> attachmentLinks) {
        this.newAttachmentLinks = attachmentLinks;
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isAcademic() {
        return isAcademic;
    }

    public void setAcademic(boolean academic) {
        isAcademic = academic;
    }
}
