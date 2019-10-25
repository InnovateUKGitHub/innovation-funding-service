package org.innovateuk.ifs.project.core.transactional;

import java.util.List;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartnerChangeServiceImpl implements PartnerChangeService {

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Override
    public ServiceResult<Void> updateProjectAfterChangingPartner(long projectId, long organisationId) {
        //resetProjectFinanceForRemainingOrganisations(projectId);
        resetProjectFinanceForRemainingOrganisationsWithService(projectId, organisationId);
        resetProjectDocuments(projectId);

        return ServiceResult.serviceSuccess();
    }

    private ServiceResult<Void> resetProjectFinanceForRemainingOrganisationsWithService(long projectId, long organisationId) {
        List<ProjectFinance> projectFinances = projectFinanceRepository.findByProjectId(projectId);

        projectFinances.forEach(projectFinance -> {
            long partnerId = projectFinance.getOrganisation().getId();
            financeCheckService.resetViability(new ProjectOrganisationCompositeId(projectId, partnerId), Viability.REVIEW,
                ViabilityRagStatus.UNSET);
            //financeCheckService.saveEligibility(new ProjectOrganisationCompositeId(projectId, partnerId), EligibilityState.REVIEW, EligibilityRagStatus
            // .UNSET);
        });
        return ServiceResult.serviceSuccess();
    }

//    private ServiceResult<Void> resetProjectFinanceForRemainingOrganisations(long projectId) {
//        List<ProjectFinance> projectFinances = projectFinanceRepository.findByProjectId(projectId);
//
//        // get organisation
//        // get current user
//        //viabilityWorkflowHandler.viabilityToReview();
//
//        projectFinances.forEach(projectFinance -> {
//            projectFinance.setViabilityStatus(ViabilityRagStatus.UNSET);
//            projectFinance.setEligibilityStatus(EligibilityRagStatus.UNSET);
//        });
//        projectFinanceRepository.save(projectFinances.get(0));
//
//        return ServiceResult.serviceSuccess();
//    }

    private ServiceResult<Void> resetProjectDocuments(long projectId) {
        List<ProjectDocument> documents = projectDocumentRepository.findAllByProjectId(projectId);

        documents.forEach(document -> document.setStatus(DocumentStatus.REJECTED));
        projectDocumentRepository.saveAll(documents);

        return ServiceResult.serviceSuccess();
    }
}
