package org.innovateuk.ifs.project.notes.viewmodel;

import java.util.Map;

public class FinanceChecksNotesAddNoteViewModel {
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private Long projectId;
    private String projectName;
    private Map<Long, String> newAttachmentLinks;
    private int maxNoteWords;
    private int maxNoteCharacters;
    private int maxTitleCharacters;
    private Long organisationId;
    private String baseUrl;
    private Long applicationId;


    public FinanceChecksNotesAddNoteViewModel(String organisationName,
                                         boolean leadPartnerOrganisation,
                                         Long projectId,
                                         String projectName,
                                         Map<Long, String> newAttachmentLinks,
                                         int maxNoteWords,
                                         int maxNoteCharacters,
                                         int maxTitleCharacters,
                                         Long organisationId,
                                         String baseUrl,
                                         Long applicationId) {
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.projectId = projectId;
        this.projectName = projectName;
        this.newAttachmentLinks = newAttachmentLinks;
        this.maxNoteWords = maxNoteWords;
        this.maxNoteCharacters = maxNoteCharacters;
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

    public int getMaxNoteWords() {
        return maxNoteWords;
    }

    public void setMaxNoteWords(int maxNoteWords) {
        this.maxNoteWords = maxNoteWords;
    }

    public int getMaxNoteCharacters() {
        return maxNoteCharacters;
    }

    public void setMaxNoteCharacters(int maxNoteCharacters) {
        this.maxNoteCharacters = maxNoteCharacters;
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
