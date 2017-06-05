package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.mapper.RejectionReasonMapper;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.invite.domain.RejectionReason} data.
 */
@Service
@Transactional(readOnly = true)
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
