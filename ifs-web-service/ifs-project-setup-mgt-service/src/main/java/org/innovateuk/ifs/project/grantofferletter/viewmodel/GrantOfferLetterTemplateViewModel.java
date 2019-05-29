package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.threads.resource.NoteResource;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * View model backing the internal users' view of the Grant Offer Letter template page
 */
public class GrantOfferLetterTemplateViewModel {

    private long applicationId;
    private String projectManagerFirstName;
    private String projectManagerLastName;
    private String leadPartnerAddress;
    private String competitionName;
    private String projectName;
    private String leadOrgName;
    private List<NoteResource> notes;

    public GrantOfferLetterTemplateViewModel() {

    }

    public GrantOfferLetterTemplateViewModel(long applicationId,
                                             String projectManagerFirstName,
                                             String projectManagerLastName,
                                             String leadPartnerAddress,
                                             String competitionName,
                                             String projectName,
                                             String leadOrgName,
                                             List<NoteResource> notes) {
        this.applicationId = applicationId;
        this.projectManagerFirstName = projectManagerFirstName;
        this.projectManagerLastName = projectManagerLastName;
        this.leadPartnerAddress = leadPartnerAddress;
        this.competitionName = competitionName;
        this.projectName = projectName;
        this.leadOrgName = leadOrgName;
        this.notes = notes;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectManagerFirstName() {
        return projectManagerFirstName;
    }

    public String getProjectManagerLastName() {
        return projectManagerLastName;
    }

    public String getLeadPartnerAddress() {
        return leadPartnerAddress;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLeadOrgName() {
        return leadOrgName;
    }

    public ZonedDateTime getNow() {
        return ZonedDateTime.now();
    }

    public List<NoteResource> getNotes() {
        return notes;
    }

}
