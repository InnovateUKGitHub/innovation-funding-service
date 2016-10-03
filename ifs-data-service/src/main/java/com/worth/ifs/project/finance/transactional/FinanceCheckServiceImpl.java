package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.mapper.FinanceCheckMapper;
import com.worth.ifs.project.finance.repository.FinanceCheckRepository;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.workflow.financechecks.configuration.FinanceCheckWorkflowHandler;
import com.worth.ifs.project.repository.PartnerOrganisationRepository;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.transactional.AbstractProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.FINANCE_CHECKS_CANNOT_PROGRESS_WORKFLOW;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * A service for finance check functionality
 */
@Service
public class FinanceCheckServiceImpl extends AbstractProjectServiceImpl implements FinanceCheckService {

    @Autowired
    private FinanceCheckRepository financeCheckRepository;

    @Autowired
    private FinanceCheckMapper financeCheckMapper;

    @Autowired
    private FinanceCheckWorkflowHandler financeCheckWorkflowHandler;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Override
    public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key){
        return find(financeCheckRepository.findByProjectAndOrganisation(key.getProjectId(), key.getOrganisationId()), notFoundError(FinanceCheck.class, id)).
                andOnSuccessReturn(financeCheckMapper::mapToResource);
    }

    @Override
    public ServiceResult<FinanceCheckResource> save(FinanceCheckResource financeCheckResource) {
        FinanceCheck toSave = financeCheckMapper.mapToDomain(financeCheckResource);
        FinanceCheck saved = financeCheckRepository.save(toSave);
        return serviceSuccess(saved).andOnSuccessReturn(financeCheckMapper::mapToResource);

    }

    @Override
    public ServiceResult<FinanceCheckResource> generate(ProjectOrganisationCompositeId key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServiceResult<Void> approve(Long projectId, Long organisationId) {

        return getCurrentlyLoggedInUser().andOnSuccess(currentUser ->
               getPartnerOrganisation(projectId, organisationId).andOnSuccessReturn(partnerOrg ->
               financeCheckWorkflowHandler.approveFinanceCheckFigures(partnerOrg, currentUser)).
               andOnSuccess(workflowResult -> workflowResult ? serviceSuccess() : serviceFailure(FINANCE_CHECKS_CANNOT_PROGRESS_WORKFLOW)));
    }

    private ServiceResult<PartnerOrganisation> getPartnerOrganisation(Long projectId, Long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(PartnerOrganisation.class, projectId, organisationId));
    }
}
