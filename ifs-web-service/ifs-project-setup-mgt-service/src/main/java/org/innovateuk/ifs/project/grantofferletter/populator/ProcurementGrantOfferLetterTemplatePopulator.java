package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel.ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

        List<ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel> milestones = projectProcurementMilestoneRestService.getByProjectId(project.getId())
                .getSuccess().stream()
                .map(m -> entryForMilestone(project, m))
                .collect(Collectors.toList());

        return new ProcurementGrantOfferLetterTemplateViewModel(applicationId,
                organisations.get(0).getOrganisationName(),
                milestones);
    }

    private ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel entryForMilestone(ProjectResource project, ProjectProcurementMilestoneResource milestone) {
        String description = milestone.getTaskOrActivity() + " " + milestone.getDescription() + " " + milestone.getDeliverable();
        String successCriteria = milestone.getSuccessCriteria();
        LocalDate completionDate = project.getTargetStartDate().plusMonths(milestone.getMonth());
        return new ProcurementGrantOfferLetterTemplateMilestoneEntryViewModel(description, successCriteria, completionDate);
    }
}
