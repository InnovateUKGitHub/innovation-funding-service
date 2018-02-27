package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
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
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    protected abstract Class<T> getInviteClass();

    protected abstract InviteRepository<T> getRepository();

    protected Supplier<ServiceResult<T>> invite(String hash) {
        return () -> getByHash(hash);
    }

    protected ServiceResult<T> getByHash(String hash) {
        return find(getRepository().getByHash(hash), notFoundError(getInviteClass(), hash));
    }

    protected ServiceResult<T> getById(long id) {
        return find(getRepository().findOne(id), notFoundError(getInviteClass(), id));
    }

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(
            value = "CHECK_EXISTING_USER_ON_HASH",
            description = "The System Registration user can check for the presence of a User on an invite or the " +
                    "presence of a User with the invited e-mail address",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method " +
                    "would be to have been given the hash in the first place"
    )
    public ServiceResult<Boolean> checkExistingUser(@P("hash") String hash) {
        return getByHash(hash).andOnSuccessReturn(invite -> {
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
