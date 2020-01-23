package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.projectteam.transactional.PendingPartnerProgressService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId.id;

@Service
public class ProjectOrganisationFinanceServiceImpl extends AbstractOrganisationFinanceService<ProjectFinanceResource> implements ProjectOrganisationFinanceService {

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private PendingPartnerProgressService pendingPartnerProgressService;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    protected ServiceResult<ProjectFinanceResource> getFinance(long projectId, long organisationId) {
        return projectFinanceService.financeChecksDetails(projectId, organisationId);
    }

    @Override
    protected ServiceResult<Void> updateFinance(ProjectFinanceResource finance) {
        return projectFinanceService.updateProjectFinance(finance);
    }

    @Override
    protected ServiceResult<FinanceRowItem> saveGrantClaim(GrantClaim grantClaim) {
        return projectFinanceRowService.update(grantClaim.getId(), grantClaim);
    }

    @Override
    protected ServiceResult<CompetitionResource> getCompetitionFromTargetId(long projectId) {
        return projectService.getProjectById(projectId)
                .andOnSuccess(project -> competitionService.getCompetitionById(project.getCompetition()));
    }

    @Override
    protected void resetYourFundingSection(ProjectFinanceResource projectFinanceResource, long competitionId, long userId) {
        if (pendingPartnerProgressService.getPendingPartnerProgress(id(projectFinanceResource.getProject(),
                projectFinanceResource.getOrganisation())).isFailure()) {
            projectFinanceService.updateProjectFinance(projectFinanceResource);
        } else {
            pendingPartnerProgressService.markYourFundingIncomplete(ProjectOrganisationCompositeId.id(projectFinanceResource.getProject(),
                    projectFinanceResource.getOrganisation())).getSuccess();
        }
    }
}