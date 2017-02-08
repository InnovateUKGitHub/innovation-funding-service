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
    private Map<Long, String> attachmentLinks;
    private int maxQueryWords;
    private int maxQueryCharacters;
    private Long queryId;
    private String querySection;

    public ProjectFinanceChecksViewModel(ProjectResource project, OrganisationResource organisation,
                                         List<ThreadViewModel> queries, boolean approved,
                                         Map<Long, String> attachmentLinks,
                                         int maxQueryWords,
                                         int maxQueryCharacters,
                                         Long queryId) {
        this.projectId = project.getId();
        this.organisationId = organisation.getId();
        this.projectName = project.getName();
        this.queries = queries;
        this.approved = approved;
        this.attachmentLinks = attachmentLinks;
        this.maxQueryWords = maxQueryWords;
        this.maxQueryCharacters = maxQueryCharacters;
        this.queryId = queryId;
        this.querySection = querySection;
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


    public Map<Long, String> getAttachmentLinks() {
        return attachmentLinks;
    }

    public void setAttachmentLinks(Map<Long, String> attachmentLinks) {
        this.attachmentLinks = attachmentLinks;
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

    public String getQuerySection() {
        return querySection;
    }

    public void setQuerySection(String querySection) {
        this.querySection = querySection;
    }
}
