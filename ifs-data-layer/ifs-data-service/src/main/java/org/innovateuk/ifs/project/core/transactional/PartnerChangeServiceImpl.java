package org.innovateuk.ifs.project.core.transactional;

import java.util.List;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartnerChangeServiceImpl implements PartnerChangeService {

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectDocumentRepository projectDocumentRepository;

    @Override
    public ServiceResult<Void> updateProjectRemovedPartner(long projectId, long organisationId) {
        resetProjectFinance(projectId, organisationId);
        resetProjectDocuments(projectId);

        return ServiceResult.serviceSuccess();
    }

    private ServiceResult<Void> resetProjectFinance(long projectId, long organisationId) {
        ProjectFinance projectFinance = projectFinanceRepository.findByProjectIdAndOrganisationId(projectId,
            organisationId);
        projectFinance.setViabilityStatus(ViabilityRagStatus.UNSET);
        projectFinance.setEligibilityStatus(EligibilityRagStatus.UNSET);

        projectFinanceRepository.save(projectFinance);

        return ServiceResult.serviceSuccess();
    }

    private ServiceResult<Void> resetProjectDocuments(long projectId) {
        List<ProjectDocument> documents = projectDocumentRepository.findAllByProjectId(projectId);

        documents.forEach(document -> document.setStatus(DocumentStatus.REJECTED));

        projectDocumentRepository.saveAll(documents);

        return ServiceResult.serviceSuccess();
    }
}
