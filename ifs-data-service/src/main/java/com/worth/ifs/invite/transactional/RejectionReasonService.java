package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.commons.security.NotSecured;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.invite.domain.RejectionReason} data.
 */
public interface RejectionReasonService {

    @NotSecured(value = "Anyone can view all active rejection reasons", mustBeSecuredByOtherServices = false)
    ServiceResult<List<RejectionReasonResource>> findAllActive();

    @NotSecured(value = "Anyone can view a rejection reason", mustBeSecuredByOtherServices = false)
    ServiceResult<RejectionReasonResource> findById(final Long id);

}