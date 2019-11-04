package org.innovateuk.ifs.project.core.transactional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;


import java.util.List;
import java.util.stream.Collectors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
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
    private FinanceCheckService financeCheckService;

    @Override
    @Transactional
    public ServiceResult<Void> updateProjectWhenPartnersChange(long projectId) {
        return resetProjectFinance(projectId).andOnSuccess(this::rejectProjectDocuments);
    }

    private ServiceResult<Long> resetProjectFinance(long projectId) {
        List<ProjectFinance> projectFinances = projectFinanceRepository.findByProjectId(projectId);

        projectFinances.forEach(projectFinance -> {
            Organisation partner = projectFinance.getOrganisation();
            long partnerId = partner.getId();
            financeCheckService.resetViability(new ProjectOrganisationCompositeId(projectId, partnerId), Viability.REVIEW, ViabilityRagStatus.UNSET);
            financeCheckService.resetEligibility(new ProjectOrganisationCompositeId(projectId, partnerId), EligibilityState.REVIEW, EligibilityRagStatus.UNSET);
        });
        return serviceSuccess(projectId);
    }

    private ServiceResult<Void> rejectProjectDocuments(long projectId) {
        List<ProjectDocument> documents = projectDocumentRepository.findAllByProjectId(projectId).stream()
            .filter(document -> !document.getStatus().equals(DocumentStatus.REJECTED))
            .collect(toList());

        if (!documents.isEmpty()) {
            documents.forEach(notRejectedDocument -> notRejectedDocument.setStatus(DocumentStatus.REJECTED));
            projectDocumentRepository.saveAll(documents);
        }

        return serviceSuccess();
    }
}
