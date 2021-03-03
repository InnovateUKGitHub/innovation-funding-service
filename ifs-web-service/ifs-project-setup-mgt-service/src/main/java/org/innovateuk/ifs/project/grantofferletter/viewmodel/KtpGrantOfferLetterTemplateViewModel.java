package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.threads.resource.NoteResource;

import java.time.ZonedDateTime;
import java.util.List;

public class KtpGrantOfferLetterTemplateViewModel {

    private final long applicationId;
    private final String projectManagerFirstName;
    private final String projectManagerLastName;
    private final List<String> projectAddress;
    private final String competitionName;
    private final String projectName;
    private final String leadOrgName;
    private final String partnerOrgName;
    private final List<NoteResource> notes;
    private final KtpFinanceModel ktpFinanceModel;

    public KtpGrantOfferLetterTemplateViewModel(long applicationId,
                                                String projectManagerFirstName,
                                                String projectManagerLastName,
                                                List<String> projectAddress,
                                                String competitionName,
                                                String projectName,
                                                String leadOrgName,
                                                String partnerOrgName,
                                                List<NoteResource> notes,
                                                KtpFinanceModel ktpFinanceModel) {
        this.applicationId = applicationId;
        this.projectManagerFirstName = projectManagerFirstName;
        this.projectManagerLastName = projectManagerLastName;
        this.projectAddress = projectAddress;
        this.competitionName = competitionName;
        this.projectName = projectName;
        this.leadOrgName = leadOrgName;
        this.partnerOrgName = partnerOrgName;
        this.notes = notes;
        this.ktpFinanceModel = ktpFinanceModel;
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

    public List<String> getProjectAddress() {
        return projectAddress;
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

    public String getPartnerOrgName() {
        return partnerOrgName;
    }

    public ZonedDateTime getNow() {
        return ZonedDateTime.now();
    }

    public List<NoteResource> getNotes() {
        return notes;
    }

    public KtpFinanceModel getKtpFinanceModel() {
        return ktpFinanceModel;
    }
}
