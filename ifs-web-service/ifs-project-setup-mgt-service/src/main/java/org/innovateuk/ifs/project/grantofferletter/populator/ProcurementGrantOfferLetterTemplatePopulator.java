package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel.ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel.ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Component
public class ProcurementGrantOfferLetterTemplatePopulator {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    @Autowired
    private ProjectRestService projectRestService;

    public ProcurementGrantOfferLetterTemplateViewModel populate(ProjectResource project, CompetitionResource competition) {
        long applicationId = project.getApplication();
        List<PartnerOrganisationResource> organisations = partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();
        PartnerOrganisationResource organisation = organisations.get(0);

        List<ProjectProcurementMilestoneResource> milestones = projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(project.getId(), organisation.getOrganisation()).getSuccess();

        List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> milestoneEntries = milestones.stream()
                .map(m -> entryForMilestone(project, m))
                .collect(Collectors.toList());

        ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(project.getId(), organisation.getOrganisation()).getSuccess();

        List<ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel> milestoneMonths = milestoneMonths(project, milestones, projectFinanceResource);

        ProjectUserResource projectManager = projectRestService.getProjectManager(project.getId()).getSuccess();

        return new ProcurementGrantOfferLetterTemplateViewModel(applicationId,
                organisation.getOrganisationName(),
                project.getName(),
                competition.getName(),
                projectManager.getUserName(),
                milestoneEntries,
                milestoneMonths);
    }

    private ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel entryForMilestone(ProjectResource project, ProjectProcurementMilestoneResource milestone) {
        String description = milestone.getDescription();
        String task = milestone.getTaskOrActivity();
        String deliverable = milestone.getDeliverable();
        String successCriteria = milestone.getSuccessCriteria();
        Integer milestoneMonth = milestone.getMonth();
        LocalDate completionDate = project.getTargetStartDate().plusMonths(milestoneMonth);
        return new ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel(description, task, deliverable, successCriteria, milestoneMonth, completionDate);
    }

    private List<ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel> milestoneMonths(ProjectResource project,
                                                                                                  List<ProjectProcurementMilestoneResource> milestones,
                                                                                                  ProjectFinanceResource projectFinanceResource) {
        Long duration = project.getDurationInMonths();

        return LongStream.rangeClosed(1L, duration).mapToObj(month -> {

            List<ProjectProcurementMilestoneResource> milestonesForMonth = new ArrayList<>();
            List<Integer> milestoneIndices = new ArrayList<>();
            for(int i = 0; i < milestones.size(); i++) {
                ProjectProcurementMilestoneResource milestone = milestones.get(i);
                if (milestone.getMonth() == month) {
                    milestonesForMonth.add(milestone);
                    milestoneIndices.add(i + 1);
                }
            }

            BigInteger payment = milestonesForMonth.stream()
                    .map(ProjectProcurementMilestoneResource::getPayment)
                    .reduce(ProcurementGrantOfferLetterTemplatePopulator::sumOfBigIntegers)
                    .orElse(BigInteger.ZERO);

            BigInteger invoiceNet;
            if (projectFinanceResource.isVatRegistered()) {
                invoiceNet = payment.multiply(BigInteger.valueOf(5L)).divide(BigInteger.valueOf(6L));
            } else {
                invoiceNet = payment;
            }
            BigInteger invoiceVat = payment.subtract(invoiceNet);

            return new ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel(month, invoiceNet, invoiceVat, milestoneIndices);
        }).collect(Collectors.toList());
    }

    private static BigInteger sumOfBigIntegers(BigInteger p1, BigInteger p2) {
        if (p1 == null && p2 == null) {
            return BigInteger.ZERO;
        }
        if (p1 == null) {
            return p2;
        }
        if (p2 == null) {
            return p1;
        }
        return p1.add(p2);
    }
}
