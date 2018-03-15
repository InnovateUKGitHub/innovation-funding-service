package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Base service class providing common methods for
 * working in an {@link Invite} subclass' domain.
 *
 * @param <T> subclass of {@link Invite}
 */
@Transactional(readOnly = true)
public abstract class InviteService<T extends Invite> {

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    @Autowired
    protected UserRepository userRepository;

    protected abstract Class<T> getInviteClass();

    protected abstract InviteRepository<T> getInviteRepository();

    protected Supplier<ServiceResult<T>> invite(String hash) {
        return () -> getByHash(hash);
    }

    protected ServiceResult<T> getByHash(String hash) {
        return find(getInviteRepository().getByHash(hash), notFoundError(getInviteClass(), hash));
    }

    protected ServiceResult<T> getById(long id) {
        return find(getInviteRepository().findOne(id), notFoundError(getInviteClass(), id));
    }

    protected ServiceResult<Boolean> checkUserExistsForInvite(String inviteHash) {
        return getByHash(inviteHash).andOnSuccessReturn(invite -> {
            if (invite.getUser() != null) {
                return true;
            }

            return userRepository.findByEmail(invite.getEmail()).isPresent();
        });
    }

    protected Supplier<ServiceResult<User>> user(Long id) {
        return () -> getUser(id);
    }

    protected ServiceResult<User> getUser(Long id) {
        return find(userRepository.findOne(id), notFoundError(User.class, id));
    }
}
