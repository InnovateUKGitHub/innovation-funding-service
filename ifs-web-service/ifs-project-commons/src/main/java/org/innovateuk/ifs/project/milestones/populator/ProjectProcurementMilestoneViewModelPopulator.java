package org.innovateuk.ifs.project.milestones.populator;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.milestones.viewmodel.ProjectProcurementMilestoneViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectProcurementMilestoneViewModelPopulator {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceService;

    public ProjectProcurementMilestoneViewModel populate(long projectId, long organisationId, UserResource userResource, boolean editMilestones) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        ProjectProcurementMilestoneResource projectProcurementMilestoneResource = projectFinanceService.getPaymentMilestoneState(projectId, organisationId).getSuccess();

        boolean userCanEdit = userResource.isInternalUser()
                && editMilestones
                && !projectProcurementMilestoneResource.isMilestonePaymentApproved();

        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        return new ProjectProcurementMilestoneViewModel(project,
                organisationId,
                finance,
                String.format("/project-setup-management/project/%d/finance-check", projectId),
                userCanEdit);
    }
}
