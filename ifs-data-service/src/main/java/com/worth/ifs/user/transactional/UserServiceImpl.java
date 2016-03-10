package com.worth.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.domain.TokenType;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserStatus;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.stream.Collectors.toSet;

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class UserServiceImpl extends BaseTransactionalService implements UserService {
    final JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    private UserRepository repository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public ServiceResult<User> getUserByUid(final String uid) {
        return find(repository.findOneByUid(uid), notFoundError(User.class, uid));
    }

    @Override
    public ServiceResult<User> getUserById(final Long id) {
        return super.getUser(id);
    }

    @Override
    public ServiceResult<List<User>> getUserByName(final String name) {
        return find(repository.findByName(name), notFoundError(User.class, name));
    }

    @Override
    public ServiceResult<List<User>> findAll() {
        return serviceSuccess(repository.findAll());
    }

    @Override
    public ServiceResult<User> findByEmail(final String email) {
        Optional<User> user = repository.findByEmail(email);
        if(user.isPresent()){
            return serviceSuccess(user.get());
        }
        return serviceFailure(notFoundError(User.class, email));
    }

    @Override
    public ServiceResult<Set<User>> findAssignableUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<User> assignables = roles.stream()
                .filter(r -> r.getRole().getName().equals("leadapplicant") || r.getRole().getName().equals("collaborator"))
                .map(ProcessRole::getUser)
                .collect(toSet());

        return serviceSuccess(assignables);
    }

    @Override
    public ServiceResult<Set<User>> findRelatedUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);

        Set<User> related = roles.stream()
                .map(ProcessRole::getUser)
                .collect(toSet());

        return serviceSuccess(related);
    }

    @Override
    public ServiceResult<Void> sendPasswordResetNotification(User user) {
        if(UserStatus.ACTIVE.equals(user.getStatus())){
            LOG.warn("Creating token");

            String hash = RandomStringUtils.random(36, true, true);
            Token token = new Token(TokenType.RESET_PASSWORD, User.class.getName(), user.getId(), hash, factory.objectNode());
            tokenRepository.save(token);


            LOG.warn("Created token");


            return serviceSuccess();
        }else{
            return serviceFailure(notFoundError(User.class, user.getEmail(), UserStatus.ACTIVE));
        }
    }
}
