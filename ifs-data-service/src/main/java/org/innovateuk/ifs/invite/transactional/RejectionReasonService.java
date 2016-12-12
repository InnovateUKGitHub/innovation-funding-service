package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.commons.security.NotSecured;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.invite.domain.RejectionReason} data.
 */
public interface RejectionReasonService {

    @NotSecured(value = "Anyone can view all active rejection reasons", mustBeSecuredByOtherServices = false)
    ServiceResult<List<RejectionReasonResource>> findAllActive();

    @NotSecured(value = "Anyone can view a rejection reason", mustBeSecuredByOtherServices = false)
    ServiceResult<RejectionReasonResource> findById(final Long id);

}
