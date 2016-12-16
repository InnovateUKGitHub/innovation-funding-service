package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Contract;
import org.innovateuk.ifs.user.mapper.ContractMapper;
import org.innovateuk.ifs.user.repository.ContractRepository;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional service for Contract retrieval
 */
@Service
public class ContractServiceImpl extends BaseTransactionalService implements ContractService {

    @Autowired
    private ContractRepository repository;

    @Autowired
    private ContractMapper mapper;

    @Override
    public ServiceResult<ContractResource> getCurrent() {
        return find(repository.findByCurrentTrue(), notFoundError(Contract.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}
