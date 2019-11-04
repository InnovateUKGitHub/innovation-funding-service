package org.innovateuk.ifs.project.core.transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
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
    public ServiceResult<Void> updateProjectWhenPartnersChange(long projectId) {
        rejectProjectDocuments(projectId);
        return resetProjectFinance(projectId);
    }

    private ServiceResult<Void> resetProjectFinance(long projectId) {
        projectFinanceRepository.findByProjectId(projectId).forEach(projectFinance -> {
            long organisationId = projectFinance.getOrganisation().getId();
            eligibilityWorkflowHandler.eligibilityReset(getPartnerOrganisation(projectId, organisationId).getSuccess(), getCurrentlyLoggedInUser().getSuccess());
            saveEligibility(projectFinance, EligibilityRagStatus.UNSET);
        });
        return serviceSuccess();
    }

    private void saveEligibility(ProjectFinance projectFinance, EligibilityRagStatus eligibilityRagStatus) {
        if (!projectFinance.getEligibilityStatus().equals(eligibilityRagStatus)) {
            projectFinance.setEligibilityStatus(eligibilityRagStatus);
            projectFinanceRepository.save(projectFinance);
        }
    }

    private void rejectProjectDocuments(long projectId) {
        projectDocumentRepository.findAllByProjectId(projectId).stream()
            .filter(document -> !document.getStatus().equals(DocumentStatus.REJECTED)).forEach(document -> document.setStatus(DocumentStatus.REJECTED));
    }
}
