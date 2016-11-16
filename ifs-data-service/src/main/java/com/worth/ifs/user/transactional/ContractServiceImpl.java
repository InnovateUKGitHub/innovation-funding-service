package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.mapper.ContractMapper;
import com.worth.ifs.user.repository.ContractRepository;
import com.worth.ifs.user.resource.ContractResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

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
