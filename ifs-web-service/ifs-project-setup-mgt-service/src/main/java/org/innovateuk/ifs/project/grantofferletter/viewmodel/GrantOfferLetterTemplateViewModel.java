package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.threads.resource.NoteResource;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * View model backing the internal users' view of the Grant Offer Letter template page
 */
public class GrantOfferLetterTemplateViewModel {

    private final long applicationId;
    private final String projectManagerFirstName;
    private final String projectManagerLastName;
    private final List<String> projectAddress;
    private final String competitionName;
    private final String projectName;
    private final String leadOrgName;
    private final List<NoteResource> notes;
    private final Map<String, String> termsAndConditionsTemplates;
    private final IndustrialFinanceTableModel industrialFinanceTable;
    private final AcademicFinanceTableModel academicFinanceTable;
    private final SummaryFinanceTableModel summaryFinanceTable;
    private final boolean subsidyControlGOLEnabled;
    private final SubsidyControlModel subsidyControlModel;
    private final boolean isProcurement;

    public GrantOfferLetterTemplateViewModel(long applicationId,
                                             String projectManagerFirstName,
                                             String projectManagerLastName,
                                             List<String> projectAddress,
                                             String competitionName,
                                             String projectName,
                                             String leadOrgName,
                                             List<NoteResource> notes,
                                             Map<String, String> termsAndConditionsTemplates,
                                             IndustrialFinanceTableModel industrialFinanceTable,
                                             AcademicFinanceTableModel academicFinanceTable,
                                             SummaryFinanceTableModel summaryFinanceTable,
                                             boolean subsidyControlGOLEnabled,
                                             SubsidyControlModel subsidyControlModel,
                                             boolean isProcurement) {
        this.applicationId = applicationId;
        this.projectManagerFirstName = projectManagerFirstName;
        this.projectManagerLastName = projectManagerLastName;
        this.projectAddress = projectAddress;
        this.competitionName = competitionName;
        this.projectName = projectName;
        this.leadOrgName = leadOrgName;
        this.notes = notes;
        this.termsAndConditionsTemplates = termsAndConditionsTemplates;
        this.industrialFinanceTable = industrialFinanceTable;
        this.academicFinanceTable = academicFinanceTable;
        this.summaryFinanceTable = summaryFinanceTable;
        this.subsidyControlGOLEnabled = subsidyControlGOLEnabled;
        this.subsidyControlModel = subsidyControlModel;
        this.isProcurement = isProcurement;
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

    public Map<String, String> getTermsAndConditionsTemplates() {
        return termsAndConditionsTemplates;
    }

    public boolean isSingleTermsAndConditionsTemplatePresent() {
        return termsAndConditionsTemplates.size() == 1;
    }

    public String getSingleTermsAndConditionsTemplate() {
        return termsAndConditionsTemplates.entrySet().stream().findAny().get().getValue();
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

    public boolean isSubsidyControlGOLEnabled() { return subsidyControlGOLEnabled; }

    public SubsidyControlModel getSubsidyControlModel() { return subsidyControlModel; }

    public boolean isProcurement() {
        return isProcurement;
    }
}

