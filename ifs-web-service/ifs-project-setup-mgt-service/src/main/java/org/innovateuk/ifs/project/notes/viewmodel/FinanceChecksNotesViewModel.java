package org.innovateuk.ifs.project.notes.viewmodel;

import org.innovateuk.ifs.thread.viewmodel.ThreadViewModel;

import java.util.List;
import java.util.Map;

/**
 * View model backing the internal Finance Team members view of the Finance Check Queries page
 */
public class FinanceChecksNotesViewModel {
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private Long projectId;
    private String projectName;
    List<ThreadViewModel> notes;
    private Long organisationId;
    private String baseUrl;
    private Map<Long, String> newAttachmentLinks;
    private int maxNoteWords;
    private int maxNoteCharacters;
    private Long noteId;
    private Long applicationId;

    public FinanceChecksNotesViewModel(String organisationName,
                                         boolean leadPartnerOrganisation,
                                         Long projectId,
                                         String projectName,
                                         List<ThreadViewModel> notes,
                                         Long organisationId,
                                         String baseUrl,
                                         Map<Long, String> newAttachmentLinks,
                                         int maxNoteWords,
                                         int maxNoteCharacters,
                                         Long noteId,
                                         Long applicationId) {
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.projectId = projectId;
        this.projectName = projectName;
        this.notes = notes;
        this.organisationId = organisationId;
        this.baseUrl = baseUrl;
        this.newAttachmentLinks = newAttachmentLinks;
        this.maxNoteWords = maxNoteWords;
        this.maxNoteCharacters = maxNoteCharacters;
        this.noteId = noteId;
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

    public List<ThreadViewModel> getNotes() {
        return notes;
    }

    public void setNotes(List<ThreadViewModel> notes) {
        this.notes = notes;
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

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Map<Long, String> getNewAttachmentLinks() {

        return newAttachmentLinks;
    }

    public void setNewAttachmentLinks(Map<Long, String> newAttachmentLinks) {
        this.newAttachmentLinks = newAttachmentLinks;
    }

}
