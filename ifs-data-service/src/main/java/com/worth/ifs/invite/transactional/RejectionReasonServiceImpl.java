package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.RejectionReason;
import com.worth.ifs.invite.mapper.RejectionReasonMapper;
import com.worth.ifs.invite.repository.RejectionReasonRepository;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.invite.domain.RejectionReason} data.
 */
@Service
public class RejectionReasonServiceImpl implements RejectionReasonService {

    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;

    @Autowired
    private RejectionReasonMapper rejectionReasonMapper;

    @Override
    public ServiceResult<List<RejectionReasonResource>> findAllActive() {
        return serviceSuccess(simpleMap(rejectionReasonRepository.findByActiveTrueOrderByPriorityAsc(), rejectionReasonMapper::mapToResource));
    }

    @Override
    public ServiceResult<RejectionReasonResource> findById(Long id) {
        return find(rejectionReasonRepository.findOne(id), notFoundError(RejectionReason.class, id)).andOnSuccessReturn(rejectionReasonMapper::mapToResource);
    }
}