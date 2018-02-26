package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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

    public ServiceResult<Boolean> checkExistingUser(@P("hash") String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(invite -> userRepository.findByEmail(invite.getEmail()))
                .andOnSuccess(user -> serviceSuccess(user.isPresent()));
    }

    protected Supplier<ServiceResult<User>> user(Long id) {
        return () -> getUser(id);
    }

    protected ServiceResult<User> getUser(Long id) {
        return find(userRepository.findOne(id), notFoundError(User.class, id));
    }
}
