package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.UserTransactionalService;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.mapper.RoleMapper;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.Role} data.
 */
@Service
public class RoleServiceImpl extends UserTransactionalService implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ServiceResult<RoleResource> findByUserRoleType(UserRoleType userRoleType) {
        return find(roleRepository.findOneByName(userRoleType.getName()), notFoundError(Role.class, userRoleType.getName())).andOnSuccessReturn(roleMapper::mapToResource);
    }
}
