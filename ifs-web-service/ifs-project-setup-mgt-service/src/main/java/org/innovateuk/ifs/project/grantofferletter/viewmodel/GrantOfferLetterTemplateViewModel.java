package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.threads.resource.NoteResource;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * View model backing the internal users' view of the Grant Offer Letter template page
 */
public class GrantOfferLetterTemplateViewModel {

    private final long competitionId;
    private final long applicationId;
    private final String projectManagerFirstName;
    private final String projectManagerLastName;
    private final List<String> projectAddress;
    private final String competitionName;
    private final String projectName;
    private final String leadOrgName;
    private final List<NoteResource> notes;
    private final String termsAndConditionsTemplate;
    private final IndustrialFinanceTableModel industrialFinanceTable;
    private final AcademicFinanceTableModel academicFinanceTable;
    private final SummaryFinanceTableModel summaryFinanceTable;
    private final boolean isProcurement;

    public GrantOfferLetterTemplateViewModel(long competitionId, long applicationId,
                                             String projectManagerFirstName,
                                             String projectManagerLastName,
                                             List<String> projectAddress,
                                             String competitionName,
                                             String projectName,
                                             String leadOrgName,
                                             List<NoteResource> notes,
                                             String termsAndConditionsTemplate,
                                             IndustrialFinanceTableModel industrialFinanceTable,
                                             AcademicFinanceTableModel academicFinanceTable,
                                             SummaryFinanceTableModel summaryFinanceTable, boolean isProcurement) {
        this.competitionId = competitionId;
        this.applicationId = applicationId;
        this.projectManagerFirstName = projectManagerFirstName;
        this.projectManagerLastName = projectManagerLastName;
        this.projectAddress = projectAddress;
        this.competitionName = competitionName;
        this.projectName = projectName;
        this.leadOrgName = leadOrgName;
        this.notes = notes;
        this.termsAndConditionsTemplate = termsAndConditionsTemplate;
        this.industrialFinanceTable = industrialFinanceTable;
        this.academicFinanceTable = academicFinanceTable;
        this.summaryFinanceTable = summaryFinanceTable;
        this.isProcurement = isProcurement;
    }

    public long getCompetitionId() {
        return competitionId;
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

    public ZonedDateTime getNow() {
        return ZonedDateTime.now();
    }

    public List<NoteResource> getNotes() {
        return notes;
    }

    public String getTermsAndConditionsTemplate() {
        return termsAndConditionsTemplate;
    }

    public IndustrialFinanceTableModel getIndustrialFinanceTable() {
        return industrialFinanceTable;
    }

    public AcademicFinanceTableModel getAcademicFinanceTable() {
        return academicFinanceTable;
    }

    public SummaryFinanceTableModel getSummaryFinanceTable() {
        return summaryFinanceTable;
    }


    public boolean isProcurement() {
        return isProcurement;
    }
}

