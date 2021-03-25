package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import java.time.LocalDate;
import java.util.List;

public class ProcurementGrantOfferLetterTemplateViewModel {

    private final long applicationId;
    private final String organisationName;
    private final List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> milestones;

    public ProcurementGrantOfferLetterTemplateViewModel(long applicationId, String organisationName,
                                                        List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> milestones) {
        this.applicationId = applicationId;
        this.organisationName = organisationName;
        this.milestones = milestones;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> getMilestones() {
        return milestones;
    }

    public static class ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel {
        private String description;
        private String successCriteria;
        private LocalDate completionDate;

        public ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel(String description, String successCriteria, LocalDate completionDate) {
            this.description = description;
            this.successCriteria = successCriteria;
            this.completionDate = completionDate;
        }

        public String getDescription() {
            return description;
        }

        public String getSuccessCriteria() {
            return successCriteria;
        }

        public LocalDate getCompletionDate() {
            return completionDate;
        }
    }
}
