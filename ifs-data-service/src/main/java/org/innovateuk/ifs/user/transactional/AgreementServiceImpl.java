package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.mapper.AgreementMapper;
import org.innovateuk.ifs.user.repository.AgreementRepository;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional service for Agreement retrieval
 */
@Service
public class AgreementServiceImpl extends BaseTransactionalService implements AgreementService {

    @Autowired
    private AgreementRepository repository;

    @Autowired
    private AgreementMapper mapper;

    @Override
    public ServiceResult<AgreementResource> getCurrent() {
        return find(repository.findByCurrentTrue(), notFoundError(Agreement.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}
