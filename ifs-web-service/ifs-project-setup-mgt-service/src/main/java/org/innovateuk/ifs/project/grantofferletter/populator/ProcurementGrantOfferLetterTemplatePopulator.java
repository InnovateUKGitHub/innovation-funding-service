package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel.ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel.ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
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
    private ProjectRestService projectRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    public ProcurementGrantOfferLetterTemplateViewModel populate(ProjectResource project, CompetitionResource competition) {
        long applicationId = project.getApplication();
        List<PartnerOrganisationResource> organisations = partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();
        PartnerOrganisationResource organisation = organisations.get(0);

        List<ProjectProcurementMilestoneResource> milestones = projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(project.getId(), organisation.getOrganisation()).getSuccess();

        List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> milestoneEntries = milestones.stream()
                .map(m -> entryForMilestone(project, m))
                .collect(Collectors.toList());

        List<ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel> milestoneMonths = milestoneMonths(project, milestones);

        return new ProcurementGrantOfferLetterTemplateViewModel(applicationId,
                organisation.getOrganisationName(),
                milestoneEntries,
                milestoneMonths);
    }

    private ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel entryForMilestone(ProjectResource project, ProjectProcurementMilestoneResource milestone) {
        String description = milestone.getTaskOrActivity() + " " + milestone.getDescription() + " " + milestone.getDeliverable();
        String successCriteria = milestone.getSuccessCriteria();
        LocalDate completionDate = project.getTargetStartDate().plusMonths(milestone.getMonth());
        return new ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel(description, successCriteria, completionDate);
    }

    private List<ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel> milestoneMonths(ProjectResource project, List<ProjectProcurementMilestoneResource> milestones) {
        Long duration = project.getDurationInMonths();

        return LongStream.range(1L, duration + 1).mapToObj(month -> {
            List<ProjectProcurementMilestoneResource> milestonesForMonth = milestones.stream().filter(m -> month == m.getMonth()).collect(Collectors.toList());
            BigInteger payment = milestonesForMonth.stream()
                    .map(ProjectProcurementMilestoneResource::getPayment)
                    .reduce(ProcurementGrantOfferLetterTemplatePopulator::sumOfBigIntegers)
                    .orElse(BigInteger.ZERO);

            BigInteger invoiceNet = payment.multiply(BigInteger.valueOf(5L)).divide(BigInteger.valueOf(6L));
            BigInteger invoiceVat = payment.subtract(invoiceNet);

            List<Integer> milestoneNumbers = new ArrayList<>();

            return new ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel(month, invoiceNet, invoiceVat, milestoneNumbers);
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
