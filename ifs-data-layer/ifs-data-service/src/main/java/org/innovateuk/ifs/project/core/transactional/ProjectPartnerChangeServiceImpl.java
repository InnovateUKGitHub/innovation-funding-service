package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectPartnerChangeServiceImpl extends BaseTransactionalService implements ProjectPartnerChangeService {

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Override
    @Transactional
    public void updateProjectWhenPartnersChange(long projectId) {
        rejectProjectDocuments(projectId);
        resetProjectFinanceEligibility(projectId);
    }

    private void resetProjectFinanceEligibility(long projectId) {
        projectFinanceRepository.findByProjectId(projectId).forEach(projectFinance -> {
            long organisationId = projectFinance.getOrganisation().getId();
            eligibilityWorkflowHandler.eligibilityReset(getPartnerOrganisation(projectId, organisationId).getSuccess(), getCurrentlyLoggedInUser().getSuccess());
            projectFinance.setEligibilityStatus(EligibilityRagStatus.UNSET);
        });
    }

    private void rejectProjectDocuments(long projectId) {
        projectDocumentRepository.findAllByProjectId(projectId)
                .forEach(document -> document.setStatus(DocumentStatus.REJECTED_DUE_TO_TEAM_CHANGE));
    }
}
