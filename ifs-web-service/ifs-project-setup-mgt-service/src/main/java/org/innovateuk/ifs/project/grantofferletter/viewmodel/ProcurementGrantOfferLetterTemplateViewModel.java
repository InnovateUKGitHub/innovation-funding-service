package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProcurementGrantOfferLetterTemplateViewModel {

    private final long applicationId;
    private final String organisationName;
    private final List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> milestones;
    private final List<ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel> milestoneMonths;

    public ProcurementGrantOfferLetterTemplateViewModel(long applicationId, String organisationName,
                                                        List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> milestones,
                                                        List<ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel> milestoneMonths) {
        this.applicationId = applicationId;
        this.organisationName = organisationName;
        this.milestones = milestones;
        this.milestoneMonths = milestoneMonths;
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

    public List<ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel> getMilestoneMonths() {
        return milestoneMonths;
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

    public static class ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel {
        private long month;
        private BigInteger invoiceNet;
        private BigInteger vat;
        private List<Integer> milestoneNumbers;

        public ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel(long month, BigInteger invoiceNet, BigInteger vat, List<Integer> milestoneNumbers){
            this.month = month;
            this.invoiceNet = invoiceNet;
            this.vat = vat;
            this.milestoneNumbers = milestoneNumbers;
        }

        public long getMonth() {
            return month;
        }

        public BigInteger getInvoiceNet() {
            return invoiceNet;
        }

        public BigInteger getVat() {
            return vat;
        }

        public BigInteger getTotal() {
            return invoiceNet.add(vat);
        }

        public String getNumbers() {
            return String.join(", ", milestoneNumbers.stream().map(i -> i.toString()).collect(Collectors.toList()));
        }
    }
}
