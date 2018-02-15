package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Base service for dealing with {@link ApplicationInvite}.
 *
 * This should be refactored to generalise for all implementations of
 * {@link org.innovateuk.ifs.invite.domain.Invite}.
 *
 * TODO: IFS-2875 - Refactor Invite service code.
 */
abstract class BaseApplicationInviteService extends BaseTransactionalService {

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    protected Supplier<ServiceResult<ApplicationInvite>> invite(final String hash) {
        return () -> getByHash(hash);
    }

    protected ServiceResult<ApplicationInvite> getByHash(String hash) {
        return find(applicationInviteRepository.getByHash(hash), notFoundError(ApplicationInvite.class, hash));
    }
}
